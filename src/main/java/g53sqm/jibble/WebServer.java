package g53sqm.jibble;
/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of Jibble Web Server / WebServerLite.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: WebServer.java,v 1.2 2004/02/01 13:37:35 pjm2 Exp $

*/


import java.io.*;
import java.net.*;
import java.util.*;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;

/**
 * The central class to the Jibble Web Server.  This is instantiated
 * by the WebServerMain class and listens for connections on the
 * specified port number before starting a new RequestThread to
 * allow connections to be dealt with concurrently.
 * 
 * @author Copyright Paul Mutton, http://www.jibble.org/
 */
public class WebServer {    
	
    public WebServer(String rootDir, int port, String cgiBinDir, String logFile, boolean enableConsoleLogging) throws WebServerException {
        try {
            _rootDir = new File(rootDir).getCanonicalFile();
        }
        catch (IOException e) {
            throw new WebServerException("Unable to determine the canonical path of the web root directory.");
        }
        if (!_rootDir.isDirectory()) {
            throw new WebServerException("The specified root directory does not exist or is not a directory.");
        }
                
        //log file
        try {
        	_logFile = new File(logFile).getCanonicalFile();
        }
        catch (IOException e) {
            throw new WebServerException("Unable to determine the canonical path of the log file.");
        }
        if (!_logFile.isFile()) {
            throw new WebServerException("The specified log file does not exist or is not a file.");
        }
        
        _cgiBinDir = cgiBinDir;
        _enableConsoleLogging = enableConsoleLogging;
        _port = port;
    }
    
    /**
     * Configures logging
     */
    private void configureLogging() {    	        
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger jibble_logger = loggerContext.getLogger("Jibble.logger");
        
        //disable console logging
        if ( !_enableConsoleLogging ) {          	
        	ConsoleAppender<?> console = null;
        	//find all console appenders and stop them
        	for (Logger logger : loggerContext.getLoggerList()) {
        	     for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders();
        	                 index.hasNext();){
        	    	 Object enumElement = index.next();
        	    	 if (enumElement instanceof ConsoleAppender) {
        	    		 console=(ConsoleAppender<?>)enumElement;
        	    		 console.stop();
        	    	 }
        	     }
        	}        	
        }
        
        //setup file logging        
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%d{HH:mm:ss.SSS} [%-5level] %msg %n");
        encoder.start();
         
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName("FILE");
        fileAppender.setFile(_logFile.getPath());
        fileAppender.setAppend(true);
        fileAppender.setEncoder(encoder);
        jibble_logger.addAppender(fileAppender);
        fileAppender.start();                
        
    }
    
    public void activate() throws WebServerException {
        ServerSocket serverSocket = null;
        configureLogging();
        try {
            serverSocket = new ServerSocket(_port);
            System.out.println("Jibble web server (modified by Arushad Ahmed (014147) for G53SQM)");
            System.out.println("Root Directory: " + _rootDir);
            System.out.println("Port: " + _port);
            System.out.println("Cgi-bin Directory: " + _cgiBinDir);
            System.out.println("Log file: " + _logFile);
            System.out.println("Enable console logging: " + _enableConsoleLogging);
        }
        catch (Exception e) {
            throw new WebServerException("Cannot start the web server on port " + _port + ".");
        }
        
        // Keep all RequestThreads within their own thread group for tidyness.
        ThreadGroup threadGroup = new ThreadGroup("HTTP Request Thread Group");
        while (_active) {
            try {
                // Pass the socket to a new thread so that it can be dealt with
                // while we can go and get ready to accept another connection.
                Socket socket = serverSocket.accept();
                RequestThread requestThread = new RequestThread(socket, _rootDir, _cgiBinDir);
                Thread thread = new Thread(threadGroup, requestThread);
                thread.start();
            }
            catch (Exception e) {
                throw new WebServerException("Error processing new connection: " + e);
            }
        }
    }
    
    private File _rootDir;
    private int _port;
    private boolean _active = true;
    private String _cgiBinDir;
    private File _logFile;  
    private boolean _enableConsoleLogging;

}
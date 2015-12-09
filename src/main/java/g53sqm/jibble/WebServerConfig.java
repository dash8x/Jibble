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
$Id: WebServerConfig.java,v 1.2 2004/02/01 13:37:35 pjm2 Exp $

*/


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Provides configuration to the web server. This leads to a standalone
 * jar file which requires no external configuration, but perhaps it
 * may be nice one day to allow these settings to be specified
 * externally so that a bit of flexibility may be given to the user
 * (this would also reduce the class size a bit :)
 * 
 * @author Copyright Paul Mutton, http://www.jibble.org/
 */
public class WebServerConfig {
    
	//config parameters
	 private final String root_directory;
	 private final int port;	
	 private final String config_file;
	 private final String cgi_bin_directory;
	 private final String log_file;
	 private final boolean enable_console_logging;

	/**
	 * Empty Constructor
	 * Sets all parameters to default
	 */
    public WebServerConfig() {
    	this.root_directory = DEFAULT_ROOT_DIRECTORY;
    	this.port = DEFAULT_PORT;
    	this.config_file = DEFAULT_CONFIG_FILE;
    	this.cgi_bin_directory = DEFAULT_CGI_BIN_DIRECTORY;
    	this.log_file = DEFAULT_LOG_FILE;
    	this.enable_console_logging = DEFAULT_ENABLE_CONSOLE_LOGGING;
    }
    
    /**
	 * Config file constructor
	 */
    public WebServerConfig(String config_file) {
    	Properties configs = new Properties();
    	//read the config file
    	try {
			configs = readConfigFile(config_file);
		} catch (FileNotFoundException e) {
			//file not found
			System.out.println("Error: Config file " + config_file + " not found.\nWarning: Using default config settings.");
			config_file = DEFAULT_CONFIG_FILE;
		} catch (IOException | IllegalArgumentException e) {
			//error reading
			System.out.println("Error: Failed reading config file " + config_file + ".\nWarning: Using default config settings.");
			config_file = DEFAULT_CONFIG_FILE;
		}
    	
    	//get the values
    	String root_directory = configs.getProperty("root_directory", DEFAULT_ROOT_DIRECTORY);
    	String cgi_bin_directory = configs.getProperty("cgi_bin_directory", DEFAULT_CGI_BIN_DIRECTORY);
    	String log_file = configs.getProperty("log_file", DEFAULT_LOG_FILE);
    	String enable_console_logging = configs.getProperty("enable_console_logging", String.format("%s", DEFAULT_ENABLE_CONSOLE_LOGGING));    	
    	int port = DEFAULT_PORT;
    	try {
    		port = Integer.parseInt(configs.getProperty("port", Integer.toString(DEFAULT_PORT)));
    	} catch (NumberFormatException e) {
    		System.out.println("Error: Invalid port number in config file.\nWarning: Using default port " + port);
    	}

    	//set the values
    	this.root_directory = root_directory;
    	this.port = port;
    	this.config_file = config_file;
    	this.cgi_bin_directory = cgi_bin_directory.replaceFirst("^./", ""); //replace initial ./
    	this.log_file = log_file;
    	this.enable_console_logging = enable_console_logging.equals("true") ? true : enable_console_logging.equals("false") ? false : DEFAULT_ENABLE_CONSOLE_LOGGING;
    }
    
    /**
	 * Constructor
	 */
    public WebServerConfig(String root_directory, int port, String config_file, String cgi_bin_directory, String log_file, boolean enable_console_logging) {
    	this.root_directory = root_directory;
    	this.port = port;
    	this.config_file = config_file;
    	this.cgi_bin_directory = cgi_bin_directory;
    	this.log_file = log_file;
    	this.enable_console_logging = enable_console_logging;
    }
    
    /**
	 * @return the root_directory
	 */
	public String getRootDirectory() {
		return root_directory;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the config_file
	 */
	public String getConfigFile() {
		return config_file;
	}

	/**
	 * @return the cgi_bin_directory
	 */
	public String getCgiBinDirectory() {
		return cgi_bin_directory;
	}

	/**
	 * @return the log_file
	 */
	public String getLogFile() {
		return log_file;
	}

	/**
	 * @return the enable_console_logging
	 */
	public boolean getEnableConsoleLogging() {
		return enable_console_logging;
	}

	public static final String VERSION = "<a href=\"http://www.jibble.org\">Jibble Web Server 1.0</a> - An extremely small Java web server";
    
    public static final String DEFAULT_ROOT_DIRECTORY = "./webfiles";
    public static final int DEFAULT_PORT = 8088;
    
    //extra values
    public static final String DEFAULT_CONFIG_FILE = "./jibble.conf";
    public static final String DEFAULT_CGI_BIN_DIRECTORY = "cgi-bin";
    public static final String DEFAULT_LOG_FILE = "./jibble.log";
    public static final boolean DEFAULT_ENABLE_CONSOLE_LOGGING = true;
    
    public static final String[] DEFAULT_FILES = new String[] {"index.html", "index.htm", "index.shtml", "index.shtm", "index.stm", "index.sht"};
    
    public static final String LINE_SEPARATOR_STRING = "\r\n";
    public static final byte[] LINE_SEPARATOR = LINE_SEPARATOR_STRING.getBytes();    
    
    // Added <String> generic to HashSet and HashMap to ensure safe type conversion - TJB
    public static final HashSet <String> SSI_EXTENSIONS = new HashSet <String>();
    public static final HashMap <String, String> MIME_TYPES = new HashMap <String, String>();
    
    // Work out the filename extension.  If there isn't one, we keep
    // it as the empty string ("").
    public static String getExtension(java.io.File file) {
        String extension = "";
        String filename = file.getName();
        int dotPos = filename.lastIndexOf(".");
        if (dotPos >= 0) {
            extension = filename.substring(dotPos);
        }
        return extension.toLowerCase();
    }
    
    /**
     * Reads a configuration file 
     * @param file_name
     * @return
     * @throws IOException 
     */
    public static Properties readConfigFile(String file_name) throws IOException {
    	
    	Properties props = new Properties();
    	
    	File config_file = new File(file_name);
    	FileReader reader = null;
    	
    	try {
	    	//read the file	    	    	
	    	reader = new FileReader(config_file);    	 	    		    
	    	 
	    	// load the properties file:
	    	props.load(reader);
    	} finally {
    		if ( reader != null ) {
    			reader.close();
    		}
    	}
    	
    	return props;
    }
   
    static {
        
        // Set up the SSI filename extensions.
        SSI_EXTENSIONS.add(".shtml");
        SSI_EXTENSIONS.add(".shtm");
        SSI_EXTENSIONS.add(".stm");
        SSI_EXTENSIONS.add(".sht");
        
        // Set up the filename extension to mime type associations.
        
        String ps = "application/postscript";
        MIME_TYPES.put(".ai", ps);
        MIME_TYPES.put(".ps", ps);
        MIME_TYPES.put(".eps", ps);
        
        String rtf = "application/rtf";
        MIME_TYPES.put(".rtf", rtf);
        
        String au = "audio/basic";
        MIME_TYPES.put(".au", au);
        MIME_TYPES.put(".snd", au);
        
        String exe = "application/octet-stream";
        MIME_TYPES.put(".bin", exe);
        MIME_TYPES.put(".dms", exe);
        MIME_TYPES.put(".lha", exe);
        MIME_TYPES.put(".lzh", exe);
        MIME_TYPES.put(".exe", exe);
        MIME_TYPES.put(".class", exe);
        
        String doc = "application/msword";
        MIME_TYPES.put(".doc", doc);
        
        String pdf = "application/pdf";
        MIME_TYPES.put(".pdf", pdf);
        
        String ppt = "application/powerpoint";
        MIME_TYPES.put(".ppt", ppt);
        
        String smi = "application/smil";
        MIME_TYPES.put(".smi", smi);
        MIME_TYPES.put(".smil", smi);
        MIME_TYPES.put(".sml", smi);
        
        String js = "application/x-javascript";
        MIME_TYPES.put(".js", js);
        
        String zip = "application/zip";
        MIME_TYPES.put(".zip", zip);
        
        String midi = "audio/midi";
        MIME_TYPES.put(".midi", midi);
        MIME_TYPES.put(".kar", midi);
        
        String mp3 = "audio/mpeg";
        MIME_TYPES.put(".mpga", mp3);
        MIME_TYPES.put(".mp2", mp3);
        MIME_TYPES.put(".mp3", mp3);
        
        String wav = "audio/x-wav";
        MIME_TYPES.put(".wav", wav);
        
        String gif = "image/gif";
        MIME_TYPES.put(".gif", gif);
        
        String ief = "image/ief";
        MIME_TYPES.put(".ief", ief);
        
        String jpeg = "image/jpeg";
        MIME_TYPES.put(".jpeg", jpeg);
        MIME_TYPES.put(".jpg", jpeg);
        MIME_TYPES.put(".jpe", jpeg);
        
        String png = "image/png";
        MIME_TYPES.put(".png", png);
        
        String tiff = "image/tiff";
        MIME_TYPES.put(".tiff", tiff);
        MIME_TYPES.put(".tif", tiff);
        
        String vrml = "model/vrml";
        MIME_TYPES.put(".wrl", vrml);
        MIME_TYPES.put(".vrml", vrml);
        
        String css = "text/css";
        MIME_TYPES.put(".css", css);
        
        String html = "text/html";
        MIME_TYPES.put(".html", html);
        MIME_TYPES.put(".htm", html);
        MIME_TYPES.put(".shtml", html);
        MIME_TYPES.put(".shtm", html);
        MIME_TYPES.put(".stm", html);
        MIME_TYPES.put(".sht", html);
        
        String txt = "text/plain";
        MIME_TYPES.put(".txt", txt);
        MIME_TYPES.put(".inf", txt);
        MIME_TYPES.put(".nfo", txt);
        
        String xml = "text/xml";
        MIME_TYPES.put(".xml", xml);
        MIME_TYPES.put(".dtd", xml);
        
        String mpeg = "video/mpeg";
        MIME_TYPES.put(".mpeg", mpeg);
        MIME_TYPES.put(".mpg", mpeg);
        MIME_TYPES.put(".mpe", mpeg);
        
        String avi = "video/x-msvideo";
        MIME_TYPES.put(".avi", avi);
        
    }
    
}
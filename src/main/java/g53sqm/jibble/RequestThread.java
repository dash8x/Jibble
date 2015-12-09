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
$Id: RequestThread.java,v 1.2 2004/02/01 13:37:35 pjm2 Exp $

*/


import java.io.*;
import java.net.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A thread which deals with an individual request to the web server.
 * This is passed a socket from the WebServer when a connection is
 * accepted.
 * 
 * @author Copyright Paul Mutton, http://www.jibble.org/
 */
public class RequestThread implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger("Jibble.logger");
	private static final Set<String> ALLOWED_METHODS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
		     new String[] {"POST","GET","HEAD","OPTIONS"}
		)));
	

    public RequestThread(Socket socket, File rootDir, String cgiBinDir) {
        _socket = socket;
        _rootDir = rootDir;
        _cgiBinDir = cgiBinDir;
    }
    
    // handles a connction from a client.
    public void run() {
        String ip = "unknown";
        String request = "unknown";
        int bytesSent = 0;
        BufferedInputStream reader = null;
        try {
            ip = _socket.getInetAddress().getHostAddress();
            BufferedReader in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(_socket.getOutputStream());
                        
            // Read the first line from the client.
            request = in.readLine();
            //get the method
            String method = getRequestMethod(request);
            //get path
            String path = "";
            if (request != null && ALLOWED_METHODS.contains(method) && (request.endsWith(" HTTP/1.0") || request.endsWith("HTTP/1.1"))) {
            	path = getRequestPath(request);
            }
            else {
                // Invalid request type (no "GET")
            	logger.debug("{} \"{}\" {}", ip, request, 405);
                _socket.close();
                return;
            }
            
            
            //read the request
            String request_input = "";            
            char[] input_buffer = new char[4096];
            int chars_read = 0;
            while ((chars_read = in.read(input_buffer, 0, 4096)) != -1) {
            	//append to requesnt input
            	for (int i = 0; i < chars_read; i++) {
            		request_input += input_buffer[i];
            	}
            }
            //add a new line to end of request
            request_input += "\n";
            
          //Read in and store all the headers.

            // Specify String types of HasMap for safety - TJB
//          HashMap headers = new HashMap();
            HashMap <String, String> headers = new HashMap<String, String>();
            String line = null;
            //process the request
            String content = "";
            Scanner parse_request = new Scanner(request_input);
			while (parse_request.hasNextLine()) {
				line = parse_request.nextLine().trim();
				//get the request content
				if (line.equals("")) {					
					while (parse_request.hasNextLine()) { 
						content += parse_request.nextLine();
					}
					break;
				}
				int colonPos = line.indexOf(":");
				if (colonPos > 0) {
					String key = line.substring(0, colonPos);
					String value = line.substring(colonPos + 1);
					headers.put(key, value.trim());
				}

			}
            
            //put the content
            if ( !content.equals("") ) {
            	headers.put("Content", content);
            }            
            
            // URLDecocer.decode(String) is deprecated - added "UTF-8"  -  TJB
            File file = new File(_rootDir, URLDecoder.decode(path, "UTF-8"));
            
            file = file.getCanonicalFile();
            
            if (!file.toString().startsWith(_rootDir.toString())) {
                // Uh-oh, it looks like some lamer is trying to take a peek
                // outside of our web root directory.
            	logger.debug("{} \"{}\" {}", ip, request, 404);
                out.write(("HTTP/1.0 403 Forbidden\r\n" +
                           "Content-Type: text/html\r\n" + 
                           "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                           "\r\n" +
                           "<h1>403 Forbidden</h1><code>" + path  + "</code><p><hr>" +
                           "<i>" + WebServerConfig.VERSION + "</i>").getBytes());
                out.flush();
                _socket.close();
                return;
            }
            
            if (file.isDirectory()) {
                // Check to see if there are any index files in the directory.
                for (int i = 0; i < WebServerConfig.DEFAULT_FILES.length; i++) {
                    File indexFile = new File(file, WebServerConfig.DEFAULT_FILES[i]);
                    if (indexFile.exists() && !indexFile.isDirectory()) {
                        file = indexFile;
                        break;
                    }
                }
                if (file.isDirectory()) {
                    // print directory listing
                	logger.debug("{} \"{}\" {}", ip, request, 200);
                    if (!path.endsWith("/")) {
                        path = path + "/";
                    }
                    File[] files = file.listFiles();
                    out.write(("HTTP/1.0 200 OK\r\n" +
                               "Content-Type: text/html\r\n" +
                               "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                               "\r\n" +
                               "<h1>Directory Listing</h1>" +
                               "<h3>" + path + "</h3>" +
                               "<table border=\"0\" cellspacing=\"8\">" +
                               "<tr><td><b>Filename</b><br></td><td align=\"right\"><b>Size</b></td><td><b>Last Modified</b></td></tr>" +
                               "<tr><td><b><a href=\"../\">../</b><br></td><td></td><td></td></tr>").getBytes());
                    for (int i = 0; i < files.length; i++) {
                        file = files[i];
                        if (file.isDirectory()) {
                            out.write(("<tr><td><b><a href=\"" + path + file.getName() + "/\">" + file.getName() + "/</a></b></td><td></td><td></td></tr>").getBytes());
                        }
                        else {
                            out.write(("<tr><td><a href=\"" + path + file.getName() + "\">" + file.getName() + "</a></td><td align=\"right\">" + file.length() + "</td><td>" + new Date(file.lastModified()).toString() + "</td></tr>").getBytes());
                        }
                    }
                    out.write(("</table><hr>" + 
                               "<i>" + WebServerConfig.VERSION + "</i>").getBytes());
                    out.flush();
                    _socket.close();
                    return;
                }
            }
            
            if (!file.exists()) {
                // The file was not found.
            	logger.debug("{} \"{}\" {}", ip, request, 404);
                out.write(("HTTP/1.0 404 File Not Found\r\n" + 
                           "Content-Type: text/html\r\n" +
                           "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                           "\r\n" +
                           "<h1>404 File Not Found</h1><code>" + path  + "</code><p><hr>" +
                           "<i>" + WebServerConfig.VERSION + "</i>").getBytes());
                out.flush();
                _socket.close();
                return;
            }

            String extension = WebServerConfig.getExtension(file);
            
            // Execute any files in any cgi-bin directories under the web root.
            if (file.getParent().indexOf(_cgiBinDir) >= 0) {
                try {
                    out.write("HTTP/1.0 200 OK\r\n".getBytes());
                    ServerSideScriptEngine.execute(out, headers, file, path);
                    out.flush();
                    logger.debug("{} \"{}\" {}", ip, path, 200);
                }
                catch (Throwable t) {
                    // Internal server error!
                	logger.error("{} \"{}\" {}", ip, request, 500);
                    out.write(("Content-Type: text/html\r\n\r\n" +
                               "<h1>Internal Server Error</h1><code>" + path  + "</code><hr>Your script produced the following error: -<p><pre>" +
                               t.toString() + 
                               "</pre><hr><i>" + WebServerConfig.VERSION + "</i>").getBytes());
                    out.flush();
                    _socket.close();
                    return;
                }
                out.flush();
                _socket.close();
                return;
            }

            reader = new BufferedInputStream(new FileInputStream(file));
            
            logger.debug("{} \"{}\" {}", ip, request, 200);
            String contentType = (String)WebServerConfig.MIME_TYPES.get(extension);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            out.write(("HTTP/1.0 200 OK\r\n" + 
                       "Date: " + new Date().toString() + "\r\n" +
                       "Server: JibbleWebServer/1.0\r\n" +
                       "Content-Type: " + contentType + "\r\n" +
                       "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                       "Content-Length: " + file.length() + "\r\n" +
                       "Last-modified: " + new Date(file.lastModified()).toString() + "\r\n" +
                       "\r\n").getBytes());

            if (WebServerConfig.SSI_EXTENSIONS.contains(extension)) {
                reader.close();
                ServerSideIncludeEngine.deliverDocument(out, file);
                _socket.close();
                return;
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = reader.read(buffer, 0, 4096)) != -1) {
                out.write(buffer, 0, bytesRead);
                bytesSent += bytesRead;
            }
            out.flush();
            reader.close();
            _socket.close();
            
        }
        catch (IOException e) {
        	logger.error("{} \"{}\" {}", ip, request);
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception anye) {
                    // Do nothing.
                }
            }
        }
    }
    
    /**
     * Returns the HTTP method from a string
     */
    public static String getRequestMethod(String line) {
    	String method = "";
    	if ( line != null) {
	    	try {	
	    		method = line.substring(0, line.indexOf(' '));
	    	} catch (StringIndexOutOfBoundsException e) {}
    	}
    	return method;
    }
    
    /**
     * Returns the HTTP request path from a string
     */
    public static String getRequestPath(String line) {
    	String path = "";
    	if ( line != null) {
	    	try {
	    		path = line.substring(line.indexOf(' '), line.length() - 9);
	    		path = path.trim();
	    	} catch (StringIndexOutOfBoundsException e) {}
    	}
    	return path;
    }
    
    private Socket _socket;
    private File _rootDir;
    private String _cgiBinDir;

}
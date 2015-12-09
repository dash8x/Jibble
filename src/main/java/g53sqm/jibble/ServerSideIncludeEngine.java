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
$Id: ServerSideIncludeEngine.java,v 1.2 2004/02/01 13:37:35 pjm2 Exp $

*/


import java.io.*;
import java.util.HashSet;



/**
 * Provides static methods to offer limited support for simple SSI
 * command directives.
 * 
 * @author Copyright Paul Mutton, http://www.jibble.org/
 */
public class ServerSideIncludeEngine {
    
    private ServerSideIncludeEngine() {
        // Prevent this class from being constructed.
    }
    
    // Deliver the fully processed SSI page to the client
    public static String deliverDocument(String out, File file) throws IOException {
        HashSet <File> visited = new HashSet <File> ();  // Added File generic for safe conversion - TJB
        return parse(out, visited, file);       
    }
    
    
    // Oooh scary recursion
    private static String parse(String out, HashSet <File> visited, File file) throws IOException {
    
    	// Added File generic to HashSet for safe conversion - TJB
    	
        if (!file.exists() || file.isDirectory()) {
            out += "[SSI include not found: " + file.getCanonicalPath() + "]";
            return out;
        }
        
        if (visited.contains(file)) {
            out += "[SSI circular inclusion rejected: " + file.getCanonicalPath() + "]";
            return out;
        }

        
        visited.add(file);
        
        // Work out the filename extension.  If there isn't one, we keep
        // it as the empty string ("").
        String extension = WebServerConfig.getExtension(file);
        
        if (WebServerConfig.SSI_EXTENSIONS.contains(extension)) {
            // process this ssi page line by line
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                int startIndex;
                int endIndex;
                while ((startIndex = line.indexOf("<!--#include file=\"")) >= 0) {
                    if ((endIndex = line.indexOf("\" -->", startIndex)) > startIndex) {
                        out += line.substring(0, startIndex);
                        String filename = line.substring(startIndex + 19, endIndex);
                        out = parse(out, visited, new File(file.getParentFile(), filename));
                        line = line.substring(endIndex + 5, line.length());
                    }
                    else {
                        out += line.substring(0, 19);
                        line = line.substring(19, line.length());
                    }
                }
                out += line;
                out += WebServerConfig.LINE_SEPARATOR_STRING;
            }
        }
        else {
            // just dish out the bytes
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = reader.read(buffer, 0, 4096)) != -1) {
            	for ( int i = 0; i < bytesRead; i++ ) {
                	out += (char) buffer[i];
                } 
            }
        }
        
        visited.remove(file);
        
        return out;
    }
    
}
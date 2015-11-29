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
$Id: WebServerMain.java,v 1.2 2004/02/01 13:37:35 pjm2 Exp $

*/


/**
 * This class contains the main method for the Jibble Web Server.
 * 
 * @author Copyright Paul Mutton, http://www.jibble.org/
 */
public class WebServerMain {

    public static void main(String[] args) {
        
        String configFile = WebServerConfig.DEFAULT_CONFIG_FILE;        
        
        if (args.length > 0) {
        	configFile = args[0];
        }
        
        //read the configurations
        WebServerConfig config = new WebServerConfig(configFile);
        
        try {
            WebServer server = new WebServer(config.getRootDirectory(), config.getPort(), config.getCgiBinDirectory(), config.getLogFile(), config.getEnableConsoleLogging());
            server.activate();
        }
        catch (WebServerException e) {
            System.out.println(e.toString());
        }
    }

}
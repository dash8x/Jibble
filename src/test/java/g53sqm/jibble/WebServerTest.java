package g53sqm.jibble;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WebServerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testWebServer() throws WebServerException {
		exception.expect(WebServerException.class);
	    exception.expectMessage("Unable to determine the canonical path of the web root directory.");
	    new WebServer("missing_dir?:!*", 8080, "cgi-bin", "jibble.log", true);
	    
	    exception.expect(WebServerException.class);
	    exception.expectMessage("The specified root directory does not exist or is not a directory.");
	    new WebServer("missing_dir", 8080, "cgi-bin", "jibble.log", true);
	    
	    exception.expect(WebServerException.class);
	    exception.expectMessage("Unable to determine the canonical path of the cgi-bin directory.");
	    new WebServer("webfiles", 8080, "missing_dir?:!*", "jibble.log", true);
	    
	    exception.expect(WebServerException.class);
	    exception.expectMessage("The specified cgi-bin directory does not exist or is not a directory.");
	    new WebServer("webfiles", 8080, "missing_dir", "jibble.log", true);
	    
	    exception.expect(WebServerException.class);
	    exception.expectMessage("Unable to determine the canonical path of the log file.");
	    new WebServer("webfiles", 8080, "cgi-bin", "missing-file?:!*", true);
	    
	    exception.expect(WebServerException.class);
	    exception.expectMessage("The specified log file does not exist or is not a file.");
	    new WebServer("webfiles", 8080, "cgi-bin", "missing-file", true);
	}

}

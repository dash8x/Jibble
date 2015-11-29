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
	}

}

package g53sqm.jibble;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

public class WebServerTest {
		
	@Rule
	public ExpectedException exception = ExpectedException.none();			
		
	@Test
	public void testActivate() throws WebServerException {	    	    
	    exception.expect(WebServerException.class);
	    exception.expectMessage("Cannot start the web server on port -1 .");
	    new WebServer("webfiles", -1, "cgi-bin", "jibble.log", true).activate();
	}

}

package g53sqm.jibble;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class WebServerMainTest {

	//test parameters
	private String expectedMessage;
	private String configFile;
	
	public WebServerMainTest(String expectedMessage, String configFile) {
		this.expectedMessage = expectedMessage;
		this.configFile = configFile;
	}
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();		
		
	@Test
	public void testMain() throws URISyntaxException {	
		String file = Paths.get(getClass().getResource(configFile).toURI()).toString();
	    WebServerMain.main(new String[]{file});
	    assertEquals("g53sqm.jibble.WebServerException: " + expectedMessage, systemOutRule.getLogWithNormalizedLineSeparator());
	}
	
	@Parameterized.Parameters
	public static Collection primeNumbers() {		
		return Arrays.asList(new Object[][] {			
			{ "The specified root directory does not exist or is not a directory.\n",  "/test-conf4.conf" },			
			{ "The specified cgi-bin directory does not exist or is not a directory.\n",  "/test-conf5.conf" },
			{ "The specified log file does not exist or is not a file.\n", "/test-conf6.conf" },
			{ "Cannot start the web server on port -1.\n", "/test-conf7.conf" }
		});
	}

}

package g53sqm.jibble;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class WebServerConstructorTest {

	//test parameters
	private String expectedMessage;
	private String rootDir;
	private int port;
	private String cgiBinDir;
	private String logFile;
	private boolean enableConsoleLogging;
	
	public WebServerConstructorTest(String expectedMessage, String rootDir, int port, String cgiBinDir, String logFile, boolean enableConsoleLogging) {
		this.expectedMessage = expectedMessage;
		this.rootDir = rootDir;
		this.port = port;
		this.cgiBinDir = cgiBinDir;
		this.logFile = logFile;
		this.enableConsoleLogging = enableConsoleLogging;
	}
	
	@Rule
	public ExpectedException exception = ExpectedException.none();			
		
	@Test
	public void testWebServer() throws WebServerException {	    	    
	    exception.expect(WebServerException.class);
	    exception.expectMessage(expectedMessage);
	    new WebServer(rootDir, port, cgiBinDir, logFile, enableConsoleLogging);
	}
	
	@Parameterized.Parameters
	public static Collection primeNumbers() {		
		return Arrays.asList(new Object[][] {
			{ "Unable to determine the canonical path of the web root directory.", "\0/><|:&missing_dir?:!*", 8080, "cgi-bin", "jibble.log", true },
			{ "The specified root directory does not exist or is not a directory.", "missing_dir", 8080, "cgi-bin", "jibble.log", true },
			{ "Unable to determine the canonical path of the cgi-bin directory.", "webfiles", 8080, "\0/><|:&missing_dir?:!*", "jibble.log", true },
			{ "The specified cgi-bin directory does not exist or is not a directory.", "webfiles", 8080, "missing_dir", "jibble.log", true },
			{ "Unable to determine the canonical path of the log file.", "webfiles", 8080, "cgi-bin", "\0/><|:&missing-file?:!*", true },
			{ "The specified log file does not exist or is not a file.", "webfiles", 8080, "cgi-bin", "missing-file", true }
		});
	}

}

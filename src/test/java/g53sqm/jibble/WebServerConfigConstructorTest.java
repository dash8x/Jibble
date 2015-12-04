package g53sqm.jibble;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class WebServerConfigConstructorTest {

	//parameters
	private WebServerConfig config;
	private String rootDir;
	private int port;
	private String cgiBinDir;
	private String logFile;
	private String configFile;
	private boolean enableConsoleLogging;
	private String expectedMessage;
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
	
	/**
	 * Constructor
	 */
	public WebServerConfigConstructorTest(WebServerConfig config, String rootDir, int port, String configFile, String cgiBinDir, String logFile, boolean enableConsoleLogging, String expectedMessage) {
		this.config = config;
		this.rootDir = rootDir;
		this.port = port;
		this.cgiBinDir = cgiBinDir;
		this.logFile = logFile;
		this.enableConsoleLogging = enableConsoleLogging;
		this.expectedMessage = expectedMessage;
		this.configFile = configFile;
	}
	
	@Parameterized.Parameters
	public static Collection primeNumbers() throws URISyntaxException {		
		String file1 = Paths.get(WebServerConfigConstructorTest.class.getResource("/test-conf3.conf").toURI()).toString();
		String file2 = Paths.get(WebServerConfigConstructorTest.class.getResource("/test-conf1.conf").toURI()).toString();
		return Arrays.asList(new Object[][] {
			{ new WebServerConfig(), "./webfiles", 8088, "./jibble.conf", "cgi-bin", "./jibble.log", true, "" },
			{ new WebServerConfig("./mydir", 80, "hello.conf", "bindir", "j.log", false), "./mydir", 80, "hello.conf", "bindir", "j.log", false, ""  },
			{ new WebServerConfig("conf.missing"), "./webfiles", 8088, "./jibble.conf", "cgi-bin", "./jibble.log", true, "Error: Config file conf.missing not found.\nWarning: Using default config settings.\n"  },
			{ new WebServerConfig(file1), "./webfiles", 8088, file1, "cgi", "./jibble.log", false, "Error: Invalid port number in config file.\nWarning: Using default port 8088\n"  },
			{ new WebServerConfig(file2), "./htdocs", 8000, file2, "cgi./scripts", "j.log", false, ""  },
		});
	}
	
	@Test
	public void testRootDirectory() {		
		assertEquals(rootDir, config.getRootDirectory());			
	}
	
	@Test
	public void testPort() {				
		assertEquals(port, config.getPort());				
	}
	
	@Test
	public void testConfigFile() {
		assertEquals(configFile, config.getConfigFile());			
	}
	
	@Test
	public void testCgiBinDirectory() {		
		assertEquals(cgiBinDir, config.getCgiBinDirectory());			
	}
	
	@Test
	public void testLogFile() {
		assertEquals(logFile, config.getLogFile());			
	}
	
	@Test
	public void testEnableConsoleLogging() {
		assertEquals(enableConsoleLogging, config.getEnableConsoleLogging());				
	}
	
	@Test
	public void testExpectedMessage() {
		assertEquals(expectedMessage, systemOutRule.getLogWithNormalizedLineSeparator());			
	}
	

}

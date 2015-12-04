package g53sqm.jibble;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.junit.ClassRule;
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
	private String actualMessage;
	
	@ClassRule
	public final static SystemOutRule systemOutRule = new SystemOutRule().enableLog();
	
	/**
	 * Constructor
	 */
	public WebServerConfigConstructorTest(WebServerConfig config, String rootDir, int port, String configFile,
			String cgiBinDir, String logFile, boolean enableConsoleLogging, String expectedMessage, String actualMessage) {
		this.config = config;
		this.rootDir = rootDir;
		this.port = port;
		this.cgiBinDir = cgiBinDir;
		this.logFile = logFile;
		this.enableConsoleLogging = enableConsoleLogging;
		this.expectedMessage = expectedMessage;
		this.actualMessage = actualMessage;
		this.configFile = configFile;
	}
	
	@Parameterized.Parameters
	public static Collection primeNumbers() throws URISyntaxException {		
		String file1 = Paths.get(WebServerConfigConstructorTest.class.getResource("/test-conf3.conf").toURI()).toString();
		String file2 = Paths.get(WebServerConfigConstructorTest.class.getResource("/test-conf1.conf").toURI()).toString();
		
		WebServerConfig config1 = new WebServerConfig();
		String msg1 = systemOutRule.getLogWithNormalizedLineSeparator(); systemOutRule.clearLog();
		
		WebServerConfig config2 = new WebServerConfig("./mydir", 80, "hello.conf", "bindir", "j.log", false);
		String msg2 = systemOutRule.getLogWithNormalizedLineSeparator(); systemOutRule.clearLog();
		
		WebServerConfig config3 = new WebServerConfig("conf.missing");
		String msg3 = systemOutRule.getLogWithNormalizedLineSeparator(); systemOutRule.clearLog();
		
		WebServerConfig config4 = new WebServerConfig(file1);
		String msg4 = systemOutRule.getLogWithNormalizedLineSeparator(); systemOutRule.clearLog();
		
		WebServerConfig config5 = new WebServerConfig(file2);
		String msg5 = systemOutRule.getLogWithNormalizedLineSeparator(); systemOutRule.clearLog();
		
		return Arrays.asList(new Object[][] {
			{ config1, "./webfiles", 8088, "./jibble.conf", "cgi-bin", "./jibble.log", true, "", msg1 },
			{ config2, "./mydir", 80, "hello.conf", "bindir", "j.log", false, "", msg2  },
			{ config3, "./webfiles", 8088, "./jibble.conf", "cgi-bin", "./jibble.log", true, "Error: Config file conf.missing not found.\nWarning: Using default config settings.\n", msg3 },
			{ config4, "./webfiles", 8088, file1, "cgi", "./jibble.log", false, "Error: Invalid port number in config file.\nWarning: Using default port 8088\n", msg4  },
			{ config5, "./htdocs", 8000, file2, "cgi./scripts", "j.log", false, "", msg5  },
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
		assertEquals(expectedMessage, actualMessage);			
	}
	

}

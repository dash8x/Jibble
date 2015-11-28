package g53sqm.jibble;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class WebServerConfigTest {

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
	
	@Test
	public void testWebServerConfigEmpty() {
		//test empty constructor
		WebServerConfig config1 = new WebServerConfig();
		assertEquals("./webfiles", config1.getRootDirectory());
		assertEquals(8088, config1.getPort());
		assertEquals("./jibble.conf", config1.getConfigFile());
		assertEquals("./cgi-bin", config1.getCgiBinDirectory());
		assertEquals("./jibble.log", config1.getLogFile());
		assertEquals(true, config1.getEnableConsoleLogging());				
	}
	
	@Test
	public void testWebServerConfigParams() {
		//test parameter constructor
		WebServerConfig config2 = new WebServerConfig("./mydir", 80, "hello.conf", "bindir", "j.log", false);
		assertEquals("./mydir", config2.getRootDirectory());
		assertEquals(80, config2.getPort());
		assertEquals("hello.conf", config2.getConfigFile());
		assertEquals("bindir", config2.getCgiBinDirectory());
		assertEquals("j.log", config2.getLogFile());
		assertEquals(false, config2.getEnableConsoleLogging());			
	}
		
	@Test
	public void testWebServerConfigFile() throws URISyntaxException {
		//test missing file
		WebServerConfig config1 = new WebServerConfig("conf.missing");
		assertEquals("Error: Config file conf.missing not found.\nWarning: Using default config settings.\n", systemOutRule.getLogWithNormalizedLineSeparator());
		assertEquals("./webfiles", config1.getRootDirectory());
		assertEquals(8088, config1.getPort());
		assertEquals("./jibble.conf", config1.getConfigFile());
		assertEquals("./cgi-bin", config1.getCgiBinDirectory());
		assertEquals("./jibble.log", config1.getLogFile());
		assertEquals(true, config1.getEnableConsoleLogging());
		
		//test invalid file
		systemOutRule.clearLog();
		String file = Paths.get(getClass().getResource("/test-conf3.conf").toURI()).toString();
		WebServerConfig config2 = new WebServerConfig(file);
		assertEquals("Error: Invalid port number in config file.\nWarning: Using default port 8088\n", systemOutRule.getLogWithNormalizedLineSeparator());
		assertEquals("./webfiles", config2.getRootDirectory());
		assertEquals(8088, config2.getPort());
		assertEquals(file, config2.getConfigFile());
		assertEquals("./cgi", config2.getCgiBinDirectory());
		assertEquals("./jibble.log", config2.getLogFile());
		assertEquals(true, config2.getEnableConsoleLogging());		
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testReadConfigFileFileNotFound() throws IOException {
		WebServerConfig.readConfigFile("conf.missing");
	}	
		
	@Test
	public void testReadConfigFile() throws IOException, URISyntaxException {		
		//read test file 1
		String file1 = Paths.get(getClass().getResource("/test-conf1.conf").toURI()).toString();
		Properties props1 = WebServerConfig.readConfigFile(file1);
		assertEquals("./htdocs", props1.getProperty("root_directory"));
		assertEquals("8000", props1.getProperty("port"));
		assertEquals("./cgi", props1.getProperty("cgi_bin_directory"));
		assertEquals("j.log", props1.getProperty("log_file"));
		assertEquals("false", props1.getProperty("enable_console_logging"));		
		
		//read test file 2
		String file2 = Paths.get(getClass().getResource("/test-conf2.conf").toURI()).toString();
		Properties props2 = WebServerConfig.readConfigFile(file2);
		assertEquals("./htdocs", props2.getProperty("root_directory"));
		assertNull(props2.getProperty("port"));
		assertEquals("./cgi", props2.getProperty("cgi_bin_directory"));
		assertNull(props2.getProperty("log_file"));
		assertEquals("", props2.getProperty("enable_console_logging"));
	}

}

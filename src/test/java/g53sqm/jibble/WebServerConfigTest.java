package g53sqm.jibble;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

public class WebServerConfigTest {

	//parameters
	private static Properties props1;
	private static Properties props2;
			
	@BeforeClass
	public static void setUp() throws URISyntaxException, IOException {
		String file1 = Paths.get(WebServerConfigTest.class.getResource("/test-conf1.conf").toURI()).toString();
		props1 = WebServerConfig.readConfigFile(file1);
		String file2 = Paths.get(WebServerConfigTest.class.getResource("/test-conf2.conf").toURI()).toString();
		props2 = WebServerConfig.readConfigFile(file2);
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testReadConfigFileFileNotFound() throws IOException {
		WebServerConfig.readConfigFile("conf.missing");
	}	
	
	@Test
	public void testRootDirectory() {		
		assertEquals("./htdocs", props1.getProperty("root_directory"));		
	}
	
	@Test
	public void testPort() {				
		assertEquals("8000", props1.getProperty("port"));
	}
	
	@Test
	public void testCgiBinDirectory() {		
		assertEquals("cgi./scripts", props1.getProperty("cgi_bin_directory"));
	}
	
	@Test
	public void testLogFile() {
		assertEquals("j.log", props1.getProperty("log_file"));
	}
	
	@Test
	public void testEnableConsoleLogging() {
		assertEquals("false", props1.getProperty("enable_console_logging"));
	}	
	
	@Test
	public void testRootDirectory2() {		
		assertEquals("./htdocs", props2.getProperty("root_directory"));
	}
	
	@Test
	public void testPortNull() {				
		assertNull(props2.getProperty("port"));
	}
	
	@Test
	public void testCgiBinDirectory2() {		
		assertEquals("./cgi", props2.getProperty("cgi_bin_directory"));
	}
	
	@Test
	public void testLogFileNull() {
		assertNull(props2.getProperty("log_file"));
	}
	
	@Test
	public void testEnableConsoleLogging2() {
		assertEquals("", props2.getProperty("enable_console_logging"));
	}		

}

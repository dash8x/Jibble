package g53sqm.jibble;

import static org.junit.Assert.*;

import org.junit.Test;

public class WebServerConfigTest {

	@Test
	public void testWebServerConfig() {
		//test empty constructor
		WebServerConfig config1 = new WebServerConfig();
		assertEquals("./webfiles", config1.getRootDirectory());
		assertEquals(8088, config1.getPort());
		assertEquals("./jibble.conf", config1.getConfigFile());
		assertEquals("./cgi-bin", config1.getCgiBinDirectory());
		assertEquals("./jibble.log", config1.getLogFile());
		assertEquals(true, config1.getEnableConsoleLogging());
		
		//test parameter constructor
		WebServerConfig config2 = new WebServerConfig("./mydir", 80, "hello.conf", "bindir", "j.log", false);
		assertEquals("./mydir", config2.getRootDirectory());
		assertEquals(80, config2.getPort());
		assertEquals("hello.conf", config2.getConfigFile());
		assertEquals("bindir", config2.getCgiBinDirectory());
		assertEquals("j.log", config2.getLogFile());
		assertEquals(false, config2.getEnableConsoleLogging());
	}

}

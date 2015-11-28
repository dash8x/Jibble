package g53sqm.jibble;

import static org.junit.Assert.*;

import org.junit.Test;

public class WebServerConfigTest {

	@Test
	public void testWebServerConfig() {
		//test empty constructor
		WebServerConfig config = new WebServerConfig();
		assertEquals("./webfiles", config.getRootDirectory());
		assertEquals(8088, config.getPort());
		assertEquals("./jibble.conf", config.getConfigFile());
		assertEquals("./cgi-bin", config.getCgiBinDirectory());
		assertEquals("./jibble.log", config.getLogFile());
		assertEquals(true, config.getEnableConsoleLogging());
	}

}

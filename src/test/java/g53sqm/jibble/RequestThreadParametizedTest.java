package g53sqm.jibble;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RequestThreadParametizedTest {

	//parameters
	private String httpHeader;
	private String requestPath;
	private String requestMethod;
	
	
	/**
	 * Constructor
	 */
	public RequestThreadParametizedTest(String httpHeader, String requestMethod, String requestPath) {
		this.httpHeader = httpHeader;
		this.requestPath = requestPath;
		this.requestMethod = requestMethod;
	}
	
	@Parameterized.Parameters
	public static Collection primeNumbers() {
		return Arrays.asList(new Object[][] {
			{ "POST cgi/bin HTTP/1.1", "POST", "cgi/bin" },
			{ "GET http://google.com HTTP/1.0", "GET", "http://google.com" },
			{ "OPTIONS cgi/bin HTTP/1.1", "OPTIONS", "cgi/bin" },
			{ "HEAD /test/files/test.html HTTP/1.1", "HEAD", "/test/files/test.html" },
			{ "/test/files/test.html HTTP/1.1", "", "/test/files/test.html" },
			{ "POST HTTP/1.1", "POST", "" },
			{ "POST ", "POST", "" },
			{ "POST", "", "" },
			{ "POST cgi/binHTTP/1.1", "POST", "cgi/bi" },
			{ "POSTcgi/bin HTTP/1.1", "POSTcgi/bin", "" },
			{ "POST cgi/bin", "POST", "" },
			{ "", "", "" }
		});
	}
	
	@Test
	public void testRequestMethod() {		
		assertEquals(requestMethod, RequestThread.getRequestMethod(httpHeader));			
	}
	
	@Test
	public void testRequestPath() {		
		assertEquals(requestPath, RequestThread.getRequestPath(httpHeader));			
	}
}

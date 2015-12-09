package g53sqm.jibble;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

public class RequestThreadProcessRequestTest {

	//the request thread object
	private static RequestThread request_handler; 
	
	@BeforeClass
	public static void setUp() throws URISyntaxException, IOException {
		request_handler = new RequestThread(new Socket(), new File("./webfiles"), "cgi-bin");
	}
			
	@Test
	public void testForbidden() throws IOException {		
		assertEquals("HTTP/1.0 403 Forbidden\r\n" +
                "Content-Type: text/html\r\n" + 
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "\r\n" +
                "<h1>403 Forbidden</h1><code>../src</code><p><hr>" +
                "<i>" + WebServerConfig.VERSION + "</i>", request_handler.processRequest("GET", "../src", null, "", ""));			
	}
	
	@Test
	public void testGETDirectory() throws IOException {		
		assertEquals("HTTP/1.0 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "\r\n" +
                "<h1>Directory Listing</h1>" +
                "<h3>/test_folder</h3>" +
                "<table border=\"0\" cellspacing=\"8\">" +
                "<tr><td><b>Filename</b><br></td><td align=\"right\"><b>Size</b></td><td><b>Last Modified</b></td></tr>" +
                "<tr><td><b><a href=\"../\">../</b><br></td><td></td><td></td></tr>" +
                "<tr><td><a href=\"/test_folder/hello.txt\">hello.txt</a></td><td align=\"right\">11</td><td>Wed Dec 09 22:12:49 SGT 2015</td></tr>", request_handler.processRequest("GET", "/test_folder", null, "", ""));			
	}
	
}

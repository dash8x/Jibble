package g53sqm.jibble;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

public class RequestThreadProcessRequestTest {

	//the request thread object
	private static RequestThread request_handler; 
	
	@BeforeClass
	public static void setUp() throws URISyntaxException, IOException {
		request_handler = new RequestThread(new Socket(), new File("./webfiles").getCanonicalFile(), "cgi-bin");
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
		File file = new File("./webfiles/test_folder/hello.txt");
		assertEquals("HTTP/1.0 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "\r\n" +
                "<h1>Directory Listing</h1>" +
                "<h3>/test_folder/</h3>" +
                "<table border=\"0\" cellspacing=\"8\">" +
                "<tr><td><b>Filename</b><br></td><td align=\"right\"><b>Size</b></td><td><b>Last Modified</b></td></tr>" +
                "<tr><td><b><a href=\"../\">../</b><br></td><td></td><td></td></tr>" +
                "<tr><td><a href=\"/test_folder/hello.txt\">hello.txt</a></td><td align=\"right\">11</td><td>"+new Date(file.lastModified()).toString()+"</td></tr>" +
                "</table><hr>" + 
                "<i>" + WebServerConfig.VERSION + "</i>", request_handler.processRequest("GET", "/test_folder", null, "", ""));			
	}
	
	@Test
	public void testPOSTDirectory() throws IOException {	
		File file = new File("./webfiles/test_folder/hello.txt");
		assertEquals("HTTP/1.0 201 Created\r\n" +
                "Content-Type: text/html\r\n" +
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "\r\n" +
                "<h1>Directory Listing</h1>" +
                "<h3>/test_folder/</h3>" +
                "<table border=\"0\" cellspacing=\"8\">" +
                "<tr><td><b>Filename</b><br></td><td align=\"right\"><b>Size</b></td><td><b>Last Modified</b></td></tr>" +
                "<tr><td><b><a href=\"../\">../</b><br></td><td></td><td></td></tr>" +
                "<tr><td><a href=\"/test_folder/hello.txt\">hello.txt</a></td><td align=\"right\">11</td><td>" + new Date(file.lastModified()).toString() + "</td></tr>" +
                "</table><hr>" + 
                "<i>" + WebServerConfig.VERSION + "</i>", request_handler.processRequest("POST", "/test_folder", null, "", ""));			
	}
	
	@Test
	public void testCgi500() throws IOException {
		assertEquals("HTTP/1.0 500 Internal Server Error\r\n" +
                "Content-Type: text/html\r\n\r\n" +
                "<h1>Internal Server Error</h1><code>/cgi-bin/cgi-test.bat</code><hr>Your script produced the following error: -<p><pre>" +  
                "java.lang.NullPointerException" +
                "</pre><hr><i>" + WebServerConfig.VERSION + "</i>", request_handler.processRequest("POST", "/cgi-bin/cgi-test.bat", null, "", ""));			
	}
	
	@Test
	public void testPOSTCgi() throws IOException {
		assertEquals("HTTP/1.0 201 Created\r\n\r\n" +
                "<!DOCTYPE html>\r\n" +
                "<html>Hello World</h1></html>", request_handler.processRequest("POST", "/cgi-bin/hello.php", new HashMap<String, String>(), "", ""));			
	}
	
	@Test
	public void testGETCgi() throws IOException {
		assertEquals("HTTP/1.0 200 OK\r\n\r\n" +
                "<!DOCTYPE html>\r\n" +
                "<html>Hello World</h1></html>", request_handler.processRequest("GET", "/cgi-bin/hello.php", new HashMap<String, String>(), "", ""));			
	}
	
	@Test
	public void testHEADCgi() throws IOException {
		assertEquals("HTTP/1.0 200 OK\r\n\r\n", request_handler.processRequest("HEAD", "/cgi-bin/hello.php", new HashMap<String, String>(), "", ""));			
	}
	
	@Test
	public void testHEADDirectory() throws IOException {		
		assertEquals("HTTP/1.0 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "\r\n", request_handler.processRequest("HEAD", "/test_folder", null, "", ""));			
	}
	
	@Test
	public void testServerSideIncludes() throws IOException {
		File file = new File("./webfiles/includes.shtml");
		assertEquals("HTTP/1.0 200 OK\r\n" +
				"Date: " + new Date().toString() + "\r\n" +
                "Server: JibbleWebServer/1.0\r\n" +
                "Content-Type: text/html\r\n" +
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "Last-modified: " + new Date(file.lastModified()).toString() + "\r\n" +
                "\r\n" +
				"<!DOCTYPE html>\r\n" +
				"<html>\r\n" +
				"<head>\r\n" +
				"<title>Testing Includes</title>\r\n" +
				"</head>\r\n" +
				"<body>\r\n" +
				"<p>Includes Work!</p>\r\n\r\n" +
				"</body>\r\n" +
				"</html>\r\n", request_handler.processRequest("GET", "/includes.shtml", null, "", ""));	
	}
	
	@Test
	public void testGETFile() throws IOException {
		File file = new File("./webfiles/test.html");
		assertEquals("HTTP/1.0 200 OK\r\n" +
				"Date: " + new Date().toString() + "\r\n" +
                "Server: JibbleWebServer/1.0\r\n" +
                "Content-Type: text/html\r\n" +
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "Last-modified: " + new Date(file.lastModified()).toString() + "\r\n" +
                "\r\n" +
				"<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head><title>Test</title></head>\n" +
				"<body><h1>Hello World</h1></body>\n" +
				"</html>", request_handler.processRequest("GET", "/test.html", null, "", ""));	
	}
	
	@Test
	public void testPOSTFile() throws IOException {
		File file = new File("./webfiles/test.html");
		assertEquals("HTTP/1.0 201 Created\r\n" +
				"Date: " + new Date().toString() + "\r\n" +
                "Server: JibbleWebServer/1.0\r\n" +
                "Content-Type: text/html\r\n" +
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "Last-modified: " + new Date(file.lastModified()).toString() + "\r\n" +
                "\r\n" +
				"<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head><title>Test</title></head>\n" +
				"<body><h1>Hello World</h1></body>\n" +
				"</html>", request_handler.processRequest("POST", "/test.html", null, "", ""));	
	}
	
	@Test
	public void testHEADFile() throws IOException {
		File file = new File("./webfiles/test.html");
		assertEquals("HTTP/1.0 200 OK\r\n" +
				"Date: " + new Date().toString() + "\r\n" +
                "Server: JibbleWebServer/1.0\r\n" +
                "Content-Type: text/html\r\n" +
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "Last-modified: " + new Date(file.lastModified()).toString() + "\r\n" +
                "\r\n", request_handler.processRequest("HEAD", "/test.html", null, "", ""));	
	}
	
	@Test
	public void test404() throws IOException {		
		assertEquals("HTTP/1.0 404 File Not Found\r\n" + 
                "Content-Type: text/html\r\n" +
                "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                "\r\n" +
                "<h1>404 File Not Found</h1><code>/missing_dir</code><p><hr>" +
                "<i>" + WebServerConfig.VERSION + "</i>", request_handler.processRequest("HEAD", "/missing_dir", null, "", ""));			
	}
	
}

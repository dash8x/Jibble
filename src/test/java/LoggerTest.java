package test.java;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import main.Logger;

@RunWith(Parameterized.class)
public class LoggerTest {

	//parameters
	private String inputIp;
	private String inputRequest;
	private Integer inputCode;
	private String expectedResult;
	
	public LoggerTest(String inputIp, String inputRequest, Integer inputCode, String expectedResult) {
		this.inputIp = inputIp;
		this.inputRequest = inputRequest;
		this.inputCode = inputCode;
		this.expectedResult = expectedResult;
	}
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
	
	@Test
	public void testLog() {
		String date = (new java.util.Date()).toString();
		Logger.log(inputIp, inputRequest, inputCode);
		assertEquals("[" + date + "]" + expectedResult, systemOutRule.getLogWithNormalizedLineSeparator());
	}

	@Parameterized.Parameters
	public static Collection primeNumbers() {		
		return Arrays.asList(new Object[][] {
			{ "", "", 1, "  \"\" 1\n" },
			{ "1.03.342 ", "request", 1, " 1.03.342  \"request\" 1\n" },
			{ null, "request", 1, " null \"request\" 1\n" },
			{ "ip", null, 1, " ip \"null\" 1\n" },
			{ null, null, 1, " null \"null\" 1\n" }
		});
	}
}

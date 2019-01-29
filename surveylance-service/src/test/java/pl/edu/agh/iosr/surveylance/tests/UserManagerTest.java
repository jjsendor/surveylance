package pl.edu.agh.iosr.surveylance.tests;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.iosr.surveylance.service.UserManager;
import pl.edu.agh.iosr.surveylance.service.impl.UserManagerImpl;


public class UserManagerTest {

	private UserManager userManager;
	
	/**
	 * Sets up testing environment for single test: creates manager service for
	 * user.
	 */
	@Before
	public void setUp() {
		userManager = new UserManagerImpl();
	}
	
	@Test
	public void TestGetAuthRequestUrl() {
		try {
			String nextUrl = "http://www.example.com";
			String authRequestUrl = userManager.getAuthRequestUrl(nextUrl);
			
			assertFalse(
					authRequestUrl.indexOf(URLEncoder.encode(nextUrl, "UTF-8"))
						== -1);
			URL url = new URL(authRequestUrl);
			assertNotNull(url);
		} catch (UnsupportedEncodingException e) {
			fail("Unexpected exception thrown. Message: " + e.getMessage());
		} catch (MalformedURLException e) {
			fail("Unexpected exception thrown. Message: " + e.getMessage());
		}
	}
}

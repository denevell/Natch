package org.denevell.natch.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.serv.logout.LogoutModel;
import org.denevell.natch.serv.logout.LogoutResource;
import org.denevell.natch.serv.logout.LogoutResourceReturnData;
import org.junit.Before;
import org.junit.Test;

public class LogoutResourceTests {
	
	private LogoutModel userModel;
	private LogoutResource resource;
	private HttpServletRequest requestContext;
    ResourceBundle rb = ResourceBundle.getBundle("Strings");

	@Before
	public void setup() {
		userModel = mock(LogoutModel.class);
		requestContext = mock(HttpServletRequest.class);
		resource = new LogoutResource(userModel, requestContext);
	}
	
	@Test
	public void shouldLogout() {
		// Arrange
		when(requestContext
				.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY)).thenReturn("asdf");
		when(userModel.logout("asdf")).thenReturn(true);
		
		// Act
		LogoutResourceReturnData result = resource.logout();
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldntLogoutOnModelError() {
		// Arrange
		when(requestContext
				.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY)).thenReturn("adsf");
		when(userModel.logout("asdf")).thenReturn(false);
		
		// Act
		LogoutResourceReturnData result = resource.logout();
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString("unknown_error"), result.getError());
	}
	
}
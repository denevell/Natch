package org.denevell.natch.tests.unit.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.serv.users.UsersModel;
import org.denevell.natch.serv.users.UsersREST;
import org.denevell.natch.serv.users.resources.LogoutResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class LogoutResourceTests {
	
	private UsersModel userModel;
	private UsersREST resource;
	private HttpServletRequest requestContext;
    ResourceBundle rb = Strings.getMainResourceBundle();

	@Before
	public void setup() {
		userModel = mock(UsersModel.class);
		requestContext = mock(HttpServletRequest.class);
		resource = new UsersREST(userModel, requestContext);
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
		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
	}
	
}
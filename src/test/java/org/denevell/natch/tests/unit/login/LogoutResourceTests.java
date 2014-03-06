package org.denevell.natch.tests.unit.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.users.LogoutResourceReturnData;
import org.denevell.natch.serv.users.LogoutRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class LogoutResourceTests {
	
	private LoginAuthKeysSingleton userModel;
	private LogoutRequest resource;
	private HttpServletRequest requestContext;
    ResourceBundle rb = Strings.getMainResourceBundle();

	@Before
	public void setup() {

		userModel = mock(LoginAuthKeysSingleton.class);
		requestContext = mock(HttpServletRequest.class);
		resource = new LogoutRequest(userModel, requestContext);
	}
	
	@Test
	public void shouldLogout() {
		// Arrange
		when(requestContext
				.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY)).thenReturn("asdf");
		when(userModel.retrieveUserEntity("asdf")).thenReturn(null);
		
		// Act
		LogoutResourceReturnData result = resource.logout();
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldntLogoutOnModelError() {
		// Arrange
		when(requestContext.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY)).thenReturn("adsf");
		when(userModel.retrieveUserEntity("adsf")).thenReturn(new UserEntity());
		
		// Act
		LogoutResourceReturnData result = resource.logout();
		
		// Assert
		assertFalse(result.isSuccessful());
	}
	
}
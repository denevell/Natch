package org.denevell.natch.tests.login;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.serv.logout.LogoutModel;
import org.junit.Before;
import org.junit.Test;

public class LogoutModelTests {
	
	private LogoutModel logoutModel;
	private LoginAuthKeysSingleton authKeyGenerator;

	@Before
	public void setup() {
		authKeyGenerator = mock(LoginAuthKeysSingleton.class);
		logoutModel = new LogoutModel(authKeyGenerator);
	}
	
	@Test
	public void shouldLogoutWhenAuthKeyExists() {
		// Arrange
		when(authKeyGenerator.retrieveUsername("authKey")).thenReturn(null);
		
		// Act
		boolean result = logoutModel.logout("authKey");
		
		// Assert
		assertTrue(result);
	}
	
	@Test
	public void shouldntLogoutWhenAuthKeyStillExists() {
		// Arrange
		when(authKeyGenerator.retrieveUsername("otherAuthKey")).thenReturn("something");
		
		// Act
		boolean result = logoutModel.logout("otherAuthKey");
		
		// Assert
		assertFalse(result);
	}
	
	@Test
	public void shouldntLogoutWhenAuthKeyIsNull() {
		// Arrange
		
		// Act
		boolean result = logoutModel.logout(null);
		
		// Assert
		assertFalse(result);
	}
	
	@Test
	public void shouldntLogoutWhenAuthKeyIsBlank() {
		// Arrange
		
		// Act
		boolean result = logoutModel.logout(" ");
		
		// Assert
		assertFalse(result);
	}
	
}
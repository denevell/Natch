package org.denevell.natch.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.denevell.natch.login.LoginModel;
import org.denevell.natch.login.LoginModel.LoginEnumResult;
import org.denevell.natch.login.LoginModel.LoginResult;
import org.denevell.natch.login.LoginResource;
import org.denevell.natch.login.LoginResourceInput;
import org.denevell.natch.login.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;

public class LoginResourceTests {
	
	private LoginModel userModel;

	@Before
	public void setup() {
		userModel = mock(LoginModel.class);
	}
	
	@Test
	public void shouldLoginWithUsernameAndPassword() {
		// Arrange
		LoginResource resource = new LoginResource(userModel);
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(new LoginResult(LoginEnumResult.LOGGED_IN));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldntLoginWithIncorrectUsernameAndPassword() {
		// Arrange
		LoginResource resource = new LoginResource(userModel);
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(new LoginResult(LoginEnumResult.CREDENTIALS_INCORRECT));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", "Incorrect username or password.", result.getError());
	}
	
	@Test
	public void shouldntLoginBadJsonInput() {
		// Arrange
		LoginResource resource = new LoginResource(userModel);
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(new LoginResult(LoginEnumResult.USER_INPUT_ERROR));
		
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertFalse("Fail to register", result.isSuccessful());
		assertEquals("Json error message", "Incorrect username or password.", result.getError());
	}
	
	@Test
	public void shouldntLoginWithNullInputObject() {
		// Arrange
		LoginResource resource = new LoginResource(userModel);
		
		// Act
		LoginResourceReturnData result = resource.login(null);
		
		// Assert
		assertFalse("Fail to register", result.isSuccessful());
		assertEquals("Json error message", "Incorrect username or password.", result.getError());
	}	
	
}
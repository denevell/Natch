package org.denevell.natch.tests.unit.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import org.denevell.natch.serv.login.LoginModel;
import org.denevell.natch.serv.login.LoginModel.LoginEnumResult;
import org.denevell.natch.serv.login.LoginModel.LoginResult;
import org.denevell.natch.serv.login.LoginResource;
import org.denevell.natch.serv.login.LoginResourceInput;
import org.denevell.natch.serv.login.LoginResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class LoginResourceTests {
	
	private LoginModel userModel;
	private LoginResource resource;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	@Before
	public void setup() {
		userModel = mock(LoginModel.class);
		resource = new LoginResource(userModel);
	}
	
	@Test
	public void shouldLoginWithUsernameAndPassword() {
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(new LoginResult(LoginEnumResult.LOGGED_IN, "authKey123"));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldntLoginWithIncorrectUsernameAndPassword() {
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(new LoginResult(LoginEnumResult.CREDENTIALS_INCORRECT));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.incorrect_username_or_password), result.getError());
	}
	
	@Test
	public void shouldntLoginBadJsonInput() {
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(new LoginResult(LoginEnumResult.USER_INPUT_ERROR));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertFalse("Fail to register", result.isSuccessful());
		assertEquals("Json error message", rb.getString(Strings.incorrect_username_or_password), result.getError());
	}
	
	@Test
	public void shouldntLoginWithNullInputObject() {
		// Arrange
		
		// Act
		LoginResourceReturnData result = resource.login(null);
		
		// Assert
		assertFalse("Fail to register", result.isSuccessful());
		assertEquals("Json error message", rb.getString(Strings.incorrect_username_or_password), result.getError());
	}	
	
	@Test
	public void shouldReturnLoginAuthKey() {
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(new LoginResult(LoginEnumResult.LOGGED_IN, "authKey123"));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertEquals("Auth key", "authKey123", result.getAuthKey());
	}
	
	@Test
	public void shouldntReturnLoginAuthKeyOnBadCredentials() {	
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(new LoginResult(LoginEnumResult.CREDENTIALS_INCORRECT));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertEquals("Auth key", "", result.getAuthKey());
	}
	
}
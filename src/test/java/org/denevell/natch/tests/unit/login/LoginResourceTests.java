package org.denevell.natch.tests.unit.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.serv.users.login.LoginModel;
import org.denevell.natch.serv.users.login.LoginRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Ignored during refactor
 * @author user
 *
 */
public class LoginResourceTests {
	
	private LoginModel userModel;
	private LoginRequest resource;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	@Before
	public void setup() {
		userModel = mock(LoginModel.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		resource = new LoginRequest(userModel, request);
	}
	
	@Ignore
	@Test
	public void shouldLoginWithUsernameAndPassword() {
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		//when(userModel.login("username", "password")).thenReturn(new LoginResult(LogoutModel.LOGGED_IN, "authKey123", true));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
		assertEquals("Is admin", true, result.isAdmin());
	}
	
	@Ignore
	@Test
	public void shouldLoginAsNonAdmin() {
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		//when(userModel.login("username", "password")).thenReturn(new LoginResult(LogoutModel.LOGGED_IN, "authKey123", false));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Is admin", false, result.isAdmin());
	}	
	
	@Test
	public void shouldntLoginWithIncorrectUsernameAndPassword() {
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(null);
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.incorrect_username_or_password), result.getError());
	}
	
	@Ignore
	@Test
	public void shouldReturnLoginAuthKey() {
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		//when(userModel.login("username", "password")).thenReturn(new LoginResult(LogoutModel.LOGGED_IN, "authKey123", true));
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertEquals("Auth key", "authKey123", result.getAuthKey());
	}
	
	@Test
	public void shouldntReturnLoginAuthKeyOnBadCredentials() {	
		// Arrange
		LoginResourceInput loginInput = new LoginResourceInput("username", "password");
		when(userModel.login("username", "password")).thenReturn(null);
		
		// Act
		LoginResourceReturnData result = resource.login(loginInput);
		
		// Assert
		assertEquals("Auth key", "", result.getAuthKey());
	}
	
}
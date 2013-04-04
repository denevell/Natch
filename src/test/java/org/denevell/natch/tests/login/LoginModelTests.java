package org.denevell.natch.tests.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.serv.login.LoginModel;
import org.denevell.natch.serv.login.LoginModel.LoginEnumResult;
import org.denevell.natch.serv.login.LoginModel.LoginResult;
import org.junit.Before;
import org.junit.Test;

public class LoginModelTests {
	
	private UserEntityQueries queries;
	private LoginModel loginModel;
	private LoginAuthKeysSingleton authKeyGenerator;

	@Before
	public void setup() {
		queries = mock(UserEntityQueries.class);
		authKeyGenerator = mock(LoginAuthKeysSingleton.class);
		loginModel = new LoginModel(queries, authKeyGenerator);
	}
	
	@Test
	public void shouldLoginWithUsernameAndPassword() {
		// Arrange
		when(queries.areCredentialsCorrect("username", "password")).thenReturn(true);
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", LoginEnumResult.LOGGED_IN, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithIncorrectUsernameAndPassword() {
		// Arrange
		when(queries.areCredentialsCorrect("username", "password")).thenReturn(false);
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", LoginEnumResult.CREDENTIALS_INCORRECT, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithBlanks() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login(" ", " ");
		
		// Assert
		assertEquals("Fail to register", LoginEnumResult.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithBlankUsername() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login(" ", "password");
		
		// Assert
		assertEquals("Fail to register", LoginEnumResult.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithBlankPassword() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login("username", " ");
		
		// Assert
		assertEquals("Fail to register", LoginEnumResult.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithNulls() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login(null, null);
		
		// Assert
		assertEquals("Fail to register", LoginEnumResult.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithNullUsername() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login(null, "password");
		
		// Assert
		assertEquals("Fail to register", LoginEnumResult.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithNullPassword() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login("username", null);
		
		// Assert
		assertEquals("Fail to register", LoginEnumResult.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldReturnLoginAuthKey() {
		// Arrange
		when(queries.areCredentialsCorrect("username", "password")).thenReturn(true);
		when(authKeyGenerator.generate("username")).thenReturn("authKey123");
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertEquals("authKey123", result.getAuthKey());
	}
	
	@Test
	public void shouldntReturnLoginAuthKeyOnBadCredentials() {
		// Arrange
		when(queries.areCredentialsCorrect("username", "password")).thenReturn(false);
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertTrue("Blank auth key on incorrect credentials", result.getAuthKey().length()==0);
	}
	
	@Test
	public void shouldBeLoggedInWithCorrectAuthKey() {
		// Arrange
		String authKey = "auth123";
		when(authKeyGenerator.retrieveUsername("auth123")).thenReturn("username");
		
		// Act
		String username = loginModel.loggedInAs(authKey);
		
		// Assert
		assertEquals("username", username);
	}
	
	@Test
	public void shouldntBeLoggedInWithIncorrectAuthKey() {
		// Arrange
		when(authKeyGenerator.retrieveUsername("auth123")).thenReturn("username");
		
		// Act
		String username = loginModel.loggedInAs("badAuth123");
		
		// Assert
		assertNull(username);
	}	
	
	@Test
	public void shouldntBeLoggedInWithNull() {
		// Act
		String username = loginModel.loggedInAs(null);
		
		// Assert
		assertNull(username);
	}	
}
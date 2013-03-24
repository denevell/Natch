package org.denevell.natch.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.login.LoginModel;
import org.denevell.natch.login.LoginModel.LoginResult;
import org.junit.Before;
import org.junit.Test;

public class LoginModelTests {
	
	private UserEntityQueries queries;

	@Before
	public void setup() {
		queries = mock(UserEntityQueries.class);
	}
	
	@Test
	public void shouldLoginWithUsernameAndPassword() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		when(queries.areCredentialsCorrect("username", "password")).thenReturn(true);
		
		// Act
		LoginResult result = um.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", LoginResult.LOGGED_IN, result);
	}
	
	@Test
	public void shouldntLoginWithIncorrectUsernameAndPassword() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		when(queries.areCredentialsCorrect("username", "password")).thenReturn(false);
		
		// Act
		LoginResult result = um.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", LoginResult.CREDENTIALS_INCORRECT, result);
	}
	
	@Test
	public void shouldntLoginWithBlanks() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		
		// Act
		LoginResult result = um.login(" ", " ");
		
		// Assert
		assertEquals("Fail to register", LoginResult.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntLoginWithBlankUsername() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		
		// Act
		LoginResult result = um.login(" ", "password");
		
		// Assert
		assertEquals("Fail to register", LoginResult.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntLoginWithBlankPassword() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		
		// Act
		LoginResult result = um.login("username", " ");
		
		// Assert
		assertEquals("Fail to register", LoginResult.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntLoginWithNulls() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		
		// Act
		LoginResult result = um.login(null, null);
		
		// Assert
		assertEquals("Fail to register", LoginResult.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntLoginWithNullUsername() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		
		// Act
		LoginResult result = um.login(null, "password");
		
		// Assert
		assertEquals("Fail to register", LoginResult.USER_INPUT_ERROR, result);
	}
	@Test
	public void shouldntLoginWithNullPassword() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		
		// Act
		LoginResult result = um.login("username", null);
		
		// Assert
		assertEquals("Fail to register", LoginResult.USER_INPUT_ERROR, result);
	}
	
}
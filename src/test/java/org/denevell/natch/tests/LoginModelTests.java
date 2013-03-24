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
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		when(queries.areCredentialsCorrect("username", "password")).thenReturn(true);
		
		// Act
		LoginResult result = um.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", LoginResult.LOGGED_IN, result);
	}
	
	@Test
	public void shouldntRegisterWithIncorrectUsernameAndPassword() {
		// Arrange
		LoginModel um = new LoginModel(queries);
		when(queries.areCredentialsCorrect("username", "password")).thenReturn(false);
		
		// Act
		LoginResult result = um.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", LoginResult.CREDENTIALS_INCORRECT, result);
	}
	
}
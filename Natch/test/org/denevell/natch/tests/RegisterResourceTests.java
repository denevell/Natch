package org.denevell.natch.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.denevell.natch.models.RegisterModel;
import org.denevell.natch.models.RegisterModel.RegisterResult;
import org.denevell.natch.rest.RegisterResource;
import org.denevell.natch.rest.input.RegisterInput;
import org.denevell.natch.rest.output.RegisterReturnData;
import org.junit.Before;
import org.junit.Test;

public class RegisterResourceTests {
	
	private RegisterModel userModel;

	@Before
	public void setup() {
		userModel = mock(RegisterModel.class);
	}
	
	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		RegisterResource resource = new RegisterResource(userModel);
		RegisterInput registerInput = new RegisterInput("username", "password");
		when(userModel.addUserToSystem("username", "password")).thenReturn(RegisterResult.REGISTERED);
		
		// Act
		RegisterReturnData result = resource.register(registerInput);
		
		// Assert
		assertTrue(result.isSuccessful());
	}
	
	@Test
	public void shouldntRegisterWithDuplicateUsername() {
		// Arrange
		RegisterResource resource = new RegisterResource(userModel);
		RegisterInput registerInput = new RegisterInput("username", "password");
		when(userModel.addUserToSystem("username", "password")).thenReturn(RegisterResult.DUPLICATE_USERNAME);
		
		// Act
		RegisterReturnData result = resource.register(registerInput);
		
		// Assert
		assertFalse(result.isSuccessful());
	}
	
}
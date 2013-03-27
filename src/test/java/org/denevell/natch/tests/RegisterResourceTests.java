package org.denevell.natch.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import org.denevell.natch.serv.register.RegisterModel;
import org.denevell.natch.serv.register.RegisterModel.RegisterResult;
import org.denevell.natch.serv.register.RegisterResource;
import org.denevell.natch.serv.register.RegisterResourceInput;
import org.denevell.natch.serv.register.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class RegisterResourceTests {
	
	private RegisterModel userModel;
    ResourceBundle rb = Strings.getMainResourceBundle();

	@Before
	public void setup() {
		userModel = mock(RegisterModel.class);
	}
	
	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		RegisterResource resource = new RegisterResource(userModel);
		RegisterResourceInput registerInput = new RegisterResourceInput("username", "password");
		when(userModel.addUserToSystem("username", "password")).thenReturn(RegisterResult.REGISTERED);
		
		// Act
		RegisterResourceReturnData result = resource.register(registerInput);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldntRegisterWithDuplicateUsername() {
		// Arrange
		RegisterResource resource = new RegisterResource(userModel);
		RegisterResourceInput registerInput = new RegisterResourceInput("username", "password");
		when(userModel.addUserToSystem("username", "password")).thenReturn(RegisterResult.DUPLICATE_USERNAME);
		
		// Act
		RegisterResourceReturnData result = resource.register(registerInput);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.username_already_exists), result.getError());
	}
	
	@Test
	public void shouldntRegisterWhenModelSaysBadInput() {
		// Arrange
		RegisterResource resource = new RegisterResource(userModel);
		RegisterResourceInput registerInput = new RegisterResourceInput("username", "password");
		when(userModel.addUserToSystem("username", "password")).thenReturn(RegisterResult.USER_INPUT_ERROR);
		
		// Act
		RegisterResourceReturnData result = resource.register(registerInput);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.user_pass_cannot_be_blanke), result.getError());
	}
	
	@Test
	public void shouldntRegisterWithNullInputObject() {
		// Arrange
		RegisterResource resource = new RegisterResource(userModel);
		when(userModel.addUserToSystem(null, null)).thenReturn(RegisterResult.USER_INPUT_ERROR);
		
		// Act
		RegisterResourceReturnData result = resource.register(null);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.user_pass_cannot_be_blanke), result.getError());
	}
	
	@Test
	public void shouldntRegisterWithUnknownError() {
		// Arrange
		RegisterResource resource = new RegisterResource(userModel);
		RegisterResourceInput registerInput = new RegisterResourceInput("user", "pass");
		when(userModel.addUserToSystem("user", "pass")).thenReturn(RegisterResult.UNKNOWN_ERROR);
		
		// Act
		RegisterResourceReturnData result = resource.register(registerInput);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
	}
	
}
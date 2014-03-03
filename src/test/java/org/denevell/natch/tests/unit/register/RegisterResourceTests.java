package org.denevell.natch.tests.unit.register;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.serv.users.logout.LogoutModel;
import org.denevell.natch.serv.users.register.RegisterModel;
import org.denevell.natch.serv.users.register.RegisterRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RegisterResourceTests {
	
	private RegisterModel userModel;
    private ResourceBundle rb = Strings.getMainResourceBundle();
	private HttpServletRequest requestContext;
	private RegisterRequest resource;

	@Before
	public void setup() {
		userModel = mock(RegisterModel.class);
		requestContext = mock(HttpServletRequest.class);
		resource = new RegisterRequest(userModel, requestContext);		
	}
	
	@Test
	@Ignore
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		RegisterResourceInput registerInput = new RegisterResourceInput("username", "password");
		when(userModel.addUserToSystem(new UserEntity("username", "password"))).thenReturn(RegisterModel.REGISTERED);
		
		// Act
		RegisterResourceReturnData result = resource.register(registerInput);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	@Ignore
	public void shouldntRegisterWithDuplicateUsername() {
		// Arrange
		RegisterResourceInput registerInput = new RegisterResourceInput("username", "password");
		when(userModel.addUserToSystem(new UserEntity("username", "password"))).thenReturn(RegisterModel.DUPLICATE_USERNAME);
		
		// Act
		RegisterResourceReturnData result = resource.register(registerInput);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.username_already_exists), result.getError());
	}
	
	@Test
	@Ignore
	public void shouldntRegisterWhenModelSaysBadInput() {
		// Arrange
		RegisterResourceInput registerInput = new RegisterResourceInput("username", "password");
		when(userModel.addUserToSystem(new UserEntity("username", "password"))).thenReturn(LogoutModel.USER_INPUT_ERROR);
		
		// Act
		RegisterResourceReturnData result = resource.register(registerInput);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
	}
	
	@Test
	@Ignore
	public void shouldntRegisterWithNullInputObject() {
		// Arrange
		when(userModel.addUserToSystem(null)).thenReturn(LogoutModel.USER_INPUT_ERROR);
		
		// Act
		RegisterResourceReturnData result = resource.register(null);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
	}
	
	@Test
	@Ignore
	public void shouldntRegisterWithUnknownError() {
		// Arrange
		RegisterResourceInput registerInput = new RegisterResourceInput("user", "pass");
		when(userModel.addUserToSystem(new UserEntity("username", "password"))).thenReturn(LogoutModel.UNKNOWN_ERROR);
		
		// Act
		RegisterResourceReturnData result = resource.register(registerInput);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
	}
	
}
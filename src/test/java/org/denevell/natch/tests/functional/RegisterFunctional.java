package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class RegisterFunctional {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private WebResource service;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	}

	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    RegisterResourceReturnData result = register(service, registerInput);
		
		// Assert
		assertEquals("", result.getError());
		assertTrue("Should return true as 'successful' field", result.isSuccessful());
	}
	
	@Test
	public void shouldAdminBitSet() {
		// Arrange 
	    // Act
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron", "aaron");
	    RegisterResourceReturnData result = register(service, registerInput);
		
		LoginResourceInput loginInput = new LoginResourceInput("aaron", "aaron");
		LoginResourceReturnData login = LoginFunctional.login(TestUtils.getRESTClient(), loginInput);

		// Assert
		assertTrue("Should register", result.isSuccessful());
		assertTrue("See admin bit", login.isAdmin()==true);
	}

	@Test
	public void shouldntSeeAdminBitSet() {
		// Arrange 
	    // Act
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron", "aaron");
	    RegisterResourceReturnData result = register(service, registerInput);
	    registerInput = new RegisterResourceInput("aaron1", "aaron1");
	    result = register(service, registerInput);
		
		LoginResourceInput loginInput = new LoginResourceInput("aaron1", "aaron1");
		LoginResourceReturnData login = LoginFunctional.login(TestUtils.getRESTClient(), loginInput);

		// Assert
		assertTrue("Should register", result.isSuccessful());
		assertFalse("Shouldn't see admin bit", login.isAdmin());
	}

	@Test
	public void shouldSeeErrorJsonOnExistingUsername() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");

	    // Act 
	    RegisterResourceReturnData result = register(service, registerInput);
	    RegisterResourceReturnData result2 = register(service, registerInput);	    
		
		// Assert
		assertTrue("Should return true as 'successful' field", result.isSuccessful());
		assertEquals("Should see blank error JSON", "", result.getError());
		assertFalse("Should return false as 'successful' field", result2.isSuccessful());
		assertEquals("Should see error JSON", rb.getString(Strings.username_already_exists), result2.getError());
	}	
	
	@Test
	public void shouldSeeErrorJsonOnBlanksPassed() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("", "");
	    RegisterResourceReturnData result = register(service, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnBlankUsername() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("", "passy");
	    RegisterResourceReturnData result = register(service, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnBlankPassword() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "");
	    RegisterResourceReturnData result = register(service, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnNulls() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput(null, null);
	    RegisterResourceReturnData result = register(service, registerInput);		

		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnNullUsername() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput(null, "passy");
	    RegisterResourceReturnData result = register(service, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnNullPassword() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", null);
	    RegisterResourceReturnData result = register(service, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}	

	public static RegisterResourceReturnData register(WebResource service, RegisterResourceInput registerInput) {
		RegisterResourceReturnData result = service 
	    		.path("rest").path("user")
				.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		return result;
	}	
	
	
}

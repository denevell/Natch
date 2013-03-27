package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.register.RegisterResourceInput;
import org.denevell.natch.serv.register.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class RegisterFunctional {
	
	private WebResource registerService;
    ResourceBundle rb = Strings.getMainResourceBundle();

	@Before
	public void setup() throws IOException, InterruptedException {
		registerService = TestUtils.getRESTRegisterClient();
		registerService.delete();
	}

	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    
	    // Act
		RegisterResourceReturnData result = registerService
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertEquals("", result.getError());
		assertTrue("Should return true as 'successful' field", result.isSuccessful());
	}
	
	@Test
	public void shouldSeeErrorJsonOnExistingUsername() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    
	    // Act
		RegisterResourceReturnData result = registerService
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		RegisterResourceReturnData result2 = registerService
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
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
	    
	    // Act
		RegisterResourceReturnData result = registerService
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blanke), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnBlankUsername() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("", "passy");
	    
	    // Act
		RegisterResourceReturnData result = registerService
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blanke), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnBlankPassword() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "");
	    
	    // Act
		RegisterResourceReturnData result = registerService
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blanke), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnNulls() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput(null, null);
	    
	    // Act
		RegisterResourceReturnData result = registerService
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blanke), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnNullUsername() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput(null, "passy");
	    
	    // Act
		RegisterResourceReturnData result = registerService
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blanke), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnNullPassword() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", null);
	    
	    // Act
		RegisterResourceReturnData result = registerService
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blanke), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}	
	
	public void shouldSeeJsonErrorOnGarbageInput() {
		// Deferred functional requirement
	}
	
	public void shouldSaltPassword() {
		// Completed non functional requirement
	}
	
}

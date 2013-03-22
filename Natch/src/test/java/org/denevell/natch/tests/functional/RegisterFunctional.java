package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.register.RegisterResourceInput;
import org.denevell.natch.register.RegisterResourceReturnData;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class RegisterFunctional {
	
	private WebResource service;

	@Before
	public void setup() throws IOException, InterruptedException {
		service = TestUtils.getRESTClient();
	    Process p = Runtime.getRuntime().exec("rm -f /var/lib/tomcat7/dbs/test.db");
	    p.waitFor();
	    assertEquals(0, p.exitValue());
	}

	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    
	    // Act
		RegisterResourceReturnData result = service
	    		.path("rest")
	    		.path("user")
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertEquals("", result.getError());
		assertTrue("Should return true as 'successful' field", result.isSuccessful());
	}
	
	public void shouldSeeErrorJsonOnBlanksPassed() {
		
	}

	@Test
	public void shouldSeeErrorJsonOnExistingUsername() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    
	    // Act
		RegisterResourceReturnData result = service
	    		.path("rest")
	    		.path("user")
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		RegisterResourceReturnData result2 = service
	    		.path("rest")
	    		.path("user")
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertTrue("Should return true as 'successful' field", result.isSuccessful());
		assertEquals("Should see blank error JSON", "", result.getError());
		assertFalse("Should return false as 'successful' field", result2.isSuccessful());
		assertEquals("Should see error JSON", "Username already exists.", result2.getError());
	}
	
	public void shouldSeeErrorJsonOnBadJsonPassed() {
		
	}
	
	public void shouldSaltPassword() {
		
	}
	
}

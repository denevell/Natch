package org.denevell.natch.integration;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.register.RegisterResourceInput;
import org.denevell.natch.register.RegisterResourceReturnData;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class Register {
	
	private WebResource service;

	@Before
	public void setup() {
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    service = client.resource(IntegrationTestUtils.getBaseURI());
	}

	@Test
	public void register_shouldRegisterWithUsernameAndPassword() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    
	    // Act
		RegisterResourceReturnData result = service
	    		.path("rest")
	    		.path("user")
	    		.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		
		// Assert
		assertTrue("Should return true as 'successful' field", result.isSuccessful());
	}
	
	public void register_shouldSeeErrorJsonOnBlanksPassed() {
		
	}

	public void register_shouldSeeErrorJsonOnExistingUsername() {
		
	}
	
	public void register_shouldSeeErrorJsonOnBadJsonPassed() {
		
	}
	
	public void register_shouldSaltPassword() {
		
	}
	
}

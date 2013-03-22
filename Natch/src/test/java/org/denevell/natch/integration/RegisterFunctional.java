package org.denevell.natch.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.register.RegisterResourceInput;
import org.denevell.natch.register.RegisterResourceReturnData;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class RegisterFunctional {
	
	private WebResource service;

	@Before
	public void setup() throws IOException, InterruptedException {
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    service = client.resource(IntegrationTestUtils.getBaseURI());
	    
	    Process p = Runtime.getRuntime().exec("rm -f /var/lib/tomcat7/dbs/test.db");
	    p.waitFor();
	    BufferedReader result = new BufferedReader(new  InputStreamReader(p.getErrorStream()));
	    System.out.println(result.readLine());
	    
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

	@Test
	public void register_shouldSeeErrorJsonOnExistingUsername() {
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
		assertFalse("Should return false as 'successful' field", result2.isSuccessful());
	}
	
	public void register_shouldSeeErrorJsonOnBadJsonPassed() {
		
	}
	
	public void register_shouldSaltPassword() {
		
	}
	
}

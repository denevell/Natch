package org.denevell.natch.integration;

import org.junit.Before;

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

	public void register_shouldRegisterWithUsernameAndPassword() {
		
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

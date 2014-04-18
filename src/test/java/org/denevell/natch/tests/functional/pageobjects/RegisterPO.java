package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class RegisterPO {
	
	private WebTarget mService;

	public RegisterPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_USER_SERVICE);
	}

	public RegisterResourceReturnData register(String username, String password) {
		return register(username, password, null);
	}	

	public RegisterResourceReturnData register(String username, String password, String emailRecover) {
		RegisterResourceInput registerInput = new RegisterResourceInput();
		registerInput.setUsername(username);
		registerInput.setPassword(password);
		registerInput.setRecoveryEmail(emailRecover);
		RegisterResourceReturnData result = mService 
	    		.path("rest").path("user").request()
	    		.put(Entity.entity(registerInput, MediaType.APPLICATION_JSON), 
	    				RegisterResourceReturnData.class);
		return result;
	}	
	

}

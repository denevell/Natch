package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.tests.functional.TestUtils;
import org.denevell.userservice.serv.LoginRequest.LoginResourceInput;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class UserLoginPO {
	
	private WebTarget mService;

	public UserLoginPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_USER_SERVICE);
	}

	public LoginResourceReturnData login(String username, String password) {
		LoginResourceInput loginInput = new LoginResourceInput(username, password);
		LoginResourceReturnData loginResult = mService 
	    		.path("user").path("login").request()
	    		.post(Entity.entity(loginInput, MediaType.APPLICATION_JSON), 
	    				LoginResourceReturnData.class);
		return loginResult;
	}	
	

}

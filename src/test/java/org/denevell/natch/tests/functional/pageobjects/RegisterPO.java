package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;

public class RegisterPO {
	
	private WebTarget mService;

	public RegisterPO(WebTarget service) {
		mService = service;
	}

	public RegisterResourceReturnData register(String username, String password) {
		RegisterResourceInput registerInput = new RegisterResourceInput();
		registerInput.setUsername(username);
		registerInput.setPassword(password);
		RegisterResourceReturnData result = mService 
	    		.path("rest").path("user").request()
	    		.put(Entity.entity(registerInput, MediaType.APPLICATION_JSON), 
	    				RegisterResourceReturnData.class);
		return result;
	}	
	

}

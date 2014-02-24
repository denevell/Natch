package org.denevell.natch.tests.ui.pageobjects;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;

import com.sun.jersey.api.client.WebResource;

public class RegisterPO {
	
	private WebResource mService;

	public RegisterPO(WebResource service) {
		mService = service;
	}

	public RegisterResourceReturnData register(String username, String password) {
		RegisterResourceInput registerInput = new RegisterResourceInput();
		registerInput.setUsername(username);
		registerInput.setPassword(password);
		RegisterResourceReturnData result = mService 
	    		.path("rest").path("user")
				.type(MediaType.APPLICATION_JSON)
	    		.put(RegisterResourceReturnData.class, registerInput);
		return result;
	}	
	

}

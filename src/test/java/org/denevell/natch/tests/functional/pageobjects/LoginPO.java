package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;

public class LoginPO {
	
	private WebTarget mService;

	public LoginPO(WebTarget service) {
		mService = service;
	}

	public LoginResourceReturnData login(String username, String password) {
		LoginResourceInput loginInput = new LoginResourceInput(username, password);
		LoginResourceReturnData loginResult = mService 
	    		.path("rest").path("user").path("login").request()
	    		.post(Entity.entity(loginInput, MediaType.APPLICATION_JSON), 
	    				LoginResourceReturnData.class);
		return loginResult;
	}	
	

}

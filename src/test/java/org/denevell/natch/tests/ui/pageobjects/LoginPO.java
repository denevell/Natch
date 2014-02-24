package org.denevell.natch.tests.ui.pageobjects;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;

import com.sun.jersey.api.client.WebResource;

public class LoginPO {
	
	private WebResource mService;

	public LoginPO(WebResource service) {
		mService = service;
	}

	public LoginResourceReturnData login(String username, String password) {
		LoginResourceInput loginInput = new LoginResourceInput(username, password);
		LoginResourceReturnData loginResult = mService 
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		return loginResult;
	}	
	

}

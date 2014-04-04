package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.io.users.ChangePasswordInput;

public class ChangePasswordPO {

	private WebTarget mService;

	public ChangePasswordPO(WebTarget service) {
		mService = service;
	}

	public Response change(String string, String authKey) {
		ChangePasswordInput changePassword = new ChangePasswordInput();
		changePassword.setPassword(string);
		Response res = mService 
	    	.path("rest").path("user").path("password").request()
	    	.header("AuthKey", authKey)
	    	.post(Entity.entity(changePassword, MediaType.APPLICATION_JSON));
		return res;
	}

	public Response changeAsAdmin(String username, String pass, String authKey) {
		ChangePasswordInput changePassword = new ChangePasswordInput();
		changePassword.setPassword(pass);
		Response res = mService 
	    	.path("rest").path("user").path("password").path(username).request()
	    	.header("AuthKey", authKey)
	    	.post(Entity.entity(changePassword, MediaType.APPLICATION_JSON));
		return res;
	}

}
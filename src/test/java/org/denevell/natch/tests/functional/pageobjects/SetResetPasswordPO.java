package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SetResetPasswordPO {

	private WebTarget mService;

	public SetResetPasswordPO(WebTarget service) {
		mService = service;
	}

	public Response setAsUser(String username) {
		Response res = mService 
	    	.path("rest").path("user").path("password_reset").path(username).request()
	    	.post(Entity.entity(null, MediaType.APPLICATION_JSON));
		return res;
	}

	public Response unsetAsAdmin(String user, String authKey) {
		Response res = mService 
	    	.path("rest").path("user").path("password_reset").path("remove").path(user).request()
	    	.header("AuthKey", authKey)
	    	.delete();
		return res;
	}

}
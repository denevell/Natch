package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.users.LogoutResourceReturnData;

public class LogoutPO {

	private WebTarget service;

	public LogoutPO(WebTarget service) {
		this.service = service;
	}

	public LogoutResourceReturnData logout(String authKey) {
		return service
			.path("rest").path("user").path("logout").request()
			.header("AuthKey", authKey)
	    	.delete(LogoutResourceReturnData.class);		
	}
}
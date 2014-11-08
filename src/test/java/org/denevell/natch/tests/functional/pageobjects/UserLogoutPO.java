package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.denevell.natch.tests.functional.TestUtils;
import org.denevell.userservice.serv.LogoutRequest.LogoutResourceReturnData;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class UserLogoutPO {

	private WebTarget service;

	public UserLogoutPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		service = client.target(TestUtils.URL_USER_SERVICE);
	}

	public LogoutResourceReturnData logout(String authKey) {
		return service
			.path("rest").path("user").path("logout").request()
			.header("AuthKey", authKey)
	    	.delete(LogoutResourceReturnData.class);		
	}
}
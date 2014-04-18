package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.users.LogoutResourceReturnData;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class LogoutPO {

	private WebTarget service;

	public LogoutPO() {
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
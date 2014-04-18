package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class SetResetPasswordPO {

	private WebTarget mService;

	public SetResetPasswordPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_USER_SERVICE);
	}

	public Response setAsUser(String recoveryEmail) {
		Response res = mService 
	    	.path("rest").path("user").path("password_reset").path(recoveryEmail).request()
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
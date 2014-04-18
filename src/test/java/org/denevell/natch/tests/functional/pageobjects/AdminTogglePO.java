package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.base.SuccessOrError;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class AdminTogglePO {
	
	private WebTarget mService;

	public AdminTogglePO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_USER_SERVICE);
	}

	public SuccessOrError toggle(String authKey) {
        return mService
            .path("rest").path("user").path("admin").path("toggle").path("other1").request()
            .header("AuthKey", authKey)
            .post(Entity.entity(null, MediaType.APPLICATION_JSON), SuccessOrError.class);
	}	

}

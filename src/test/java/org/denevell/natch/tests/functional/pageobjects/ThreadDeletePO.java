package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class ThreadDeletePO {
	
	private WebTarget mService;

	public ThreadDeletePO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_REST_SERVICE);
	}

	public Response delete(String threadId, String authKey) {
    return mService
		.path("rest").path("delete").path("ThreadEntity").path(threadId)
		.request()
		.header("AuthKey", authKey)
		.accept(MediaType.APPLICATION_JSON)
		.delete();
	}	
	

}

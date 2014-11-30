package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class PostsListPO {
	
	private WebTarget mService;

	public PostsListPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_REST_SERVICE);
	}

	public OutputList list(String start, String limit) {
    return mService
		.path("rest").path("post").path(start).path(limit).request()
		.accept(MediaType.APPLICATION_JSON)
		.get(OutputList.class);
	}	
	

}

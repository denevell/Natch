package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.serv.PostAddRequest.PostAddInput;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class PostAddPO {
	
	private WebTarget mService;

	public PostAddPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_REST_SERVICE);
	}

	public Response add(
	    String content, 
			String authKey,
			String threadId) {
	  PostAddInput input = new PostAddInput();
		input.content = content;
		input.threadId = threadId;
    return mService
		.path("rest").path("post").path("add").request()
		.accept(MediaType.APPLICATION_JSON)
		.header("AuthKey", authKey)
		.put(Entity.entity(input, MediaType.APPLICATION_JSON));
	}	
	

}

package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity.EditInput;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class PostEditPO {
	
	private WebTarget mService;

	public PostEditPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_REST_SERVICE);
	}

	public Response edit(
	    String content, 
	    long postId,
			String authKey) {
	  EditInput input = new EditInput();
		input.content = content;

    return mService
		.path("rest").path("update").path("PostEntity%24EditInput").path(String.valueOf(postId))
		.request()
		.header("AuthKey", authKey)
		.accept(MediaType.APPLICATION_JSON)
		.post(Entity.json(input));
    
	}	
	

}

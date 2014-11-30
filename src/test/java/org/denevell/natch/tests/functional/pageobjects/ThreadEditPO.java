package org.denevell.natch.tests.functional.pageobjects;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.ThreadEntity.EditInput;
import org.denevell.natch.entities.ThreadEntity.AddInput.StringWrapper;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class ThreadEditPO {
	
	private WebTarget mService;

	public ThreadEditPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_REST_SERVICE);
	}

	public Response edit(
	    String subject, 
	    String content, 
	    long postId,
			String authKey) {
	    return edit(subject, content, null, postId, authKey);
  }
	
	public Response edit(
	    String subject, 
	    String content, 
	    List<String> tags, 
	    long postId,
			String authKey) {
	  EditInput input = new EditInput();
		input.content = content;
		input.subject = subject;
		if(tags!=null) {
		  input.tags = StringWrapper.fromStrings(tags);
		}
    return mService
		.path("rest").path("thread_edit")
		.path(String.valueOf(postId)).request()
		.accept(MediaType.APPLICATION_JSON)
		.header("AuthKey", authKey)
    .post(Entity.entity(input, MediaType.APPLICATION_JSON));
	}	

}

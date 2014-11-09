package org.denevell.natch.tests.functional.pageobjects;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.model.ThreadEntity.AddInput;
import org.denevell.natch.model.ThreadEntity.AddInput.StringWrapper;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class ThreadAddPO {
	
	private WebTarget mService;

	public ThreadAddPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_REST_SERVICE);
	}
	
	public Response add(
	    String subject, 
	    String content, 
			String authKey) {
	  return add(subject, content, null, authKey);
	}

	public Response add(
	    String subject, 
	    String content, 
	    List<String> tags, 
			String authKey) {
	  AddInput input = new AddInput();
		input.content = content;
		input.subject = subject;
		if(tags!=null) {
		  input.tags = StringWrapper.fromStrings(tags);
		}
    return mService
		.path("rest").path("thread").request()
		.accept(MediaType.APPLICATION_JSON)
		.header("AuthKey", authKey)
		.put(Entity.entity(input, MediaType.APPLICATION_JSON));
	}	

}

package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class AddPostPO {
	
	private WebTarget mService;

	public AddPostPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_REST_SERVICE);
	}

	public AddPostResourceReturnData add(String subject, String content, String authKey) {
		return add(subject, content, null, authKey, null);
	}	

	public AddPostResourceReturnData add(String subject, String content, String[] tags, String authKey) {
		return add(subject, content, tags, authKey, null);
	}

	public AddPostResourceReturnData add(String subject, String content, String authKey, String threadId) {
		return add(subject, content, null, authKey, threadId);
	}

	public AddPostResourceReturnData add(String subject, String content, 
			String[] tags, 
			String authKey,
			String threadId) {
		AddPostResourceInput input = new AddPostResourceInput();
		input.setContent(content);
		input.setSubject(subject);
		if(tags!=null) {
			input.setTags(Lists.newArrayList(tags));
		}
		if(threadId!=null) {
			input.setThreadId(threadId);
		}
		AddPostResourceReturnData returnData = 
        mService
		.path("rest").path("post").path("add").request()
		.header("AuthKey", authKey)
		.put(Entity.entity(input, MediaType.APPLICATION_JSON), AddPostResourceReturnData.class);
        return returnData;
	}	
	

}

package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;

public class AddPostPO {
	
	private WebTarget mService;

	public AddPostPO(WebTarget service) {
		mService = service;
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

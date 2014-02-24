package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.WebResource;

public class AddPostPO {
	
	private WebResource mService;

	public AddPostPO(WebResource service) {
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
		.path("rest").path("post").path("add")
		.header("AuthKey", authKey)
		.type(MediaType.APPLICATION_JSON)
		.put(AddPostResourceReturnData.class, input);
        return returnData;
	}	
	

}

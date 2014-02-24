package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.AddThreadFromPostResourceInput;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.WebResource;

public class AddThreadFromPostPO {
	
	private WebResource mService;

	public AddThreadFromPostPO(WebResource service) {
		mService = service;
	}

    public AddPostResourceReturnData addThreadFromPost(
    		String subject, 
    		String content,
    		long postId,
    		String userId,
    		String authKey 
    		) {
        AddThreadFromPostResourceInput input = new AddThreadFromPostResourceInput();
        input.setContent(content);
        input.setSubject(subject);
        input.setPostId(postId);
        //input.setTags(tags)
        input.setUserId(userId);

		AddPostResourceReturnData returnData = 
		mService 
        .path("rest").path("thread").path("frompost")
        .header("AuthKey", authKey)
        .type(MediaType.APPLICATION_JSON)
        .put(AddPostResourceReturnData.class, input);
        return returnData;
    }	
	

}

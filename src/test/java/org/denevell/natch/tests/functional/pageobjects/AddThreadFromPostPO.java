package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.AddThreadFromPostResourceInput;

public class AddThreadFromPostPO {
	
	private WebTarget mService;

	public AddThreadFromPostPO(WebTarget service) {
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
        .path("rest").path("thread").path("frompost").request()
        .header("AuthKey", authKey)
        .put(Entity.entity(input, MediaType.APPLICATION_JSON), AddPostResourceReturnData.class);
        return returnData;
    }	
	

}

package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.ThreadFromPostRequest.AddThreadFromPostResourceInput;

public class AddThreadFromPostPO {
	
	private WebTarget mService;

	public AddThreadFromPostPO(WebTarget service) {
		mService = service;
	}

    public AddPostResourceReturnData addThreadFromPost(
    		String subject, 
    		long postId,
    		String authKey 
    		) {
        AddThreadFromPostResourceInput input = new AddThreadFromPostResourceInput();
        input.setSubject(subject);
        input.setPostId(postId);

		AddPostResourceReturnData returnData = 
		mService 
        .path("rest").path("thread").path("frompost").request()
        .header("AuthKey", authKey)
        .put(Entity.entity(input, MediaType.APPLICATION_JSON), AddPostResourceReturnData.class);
        return returnData;
    }	
	

}

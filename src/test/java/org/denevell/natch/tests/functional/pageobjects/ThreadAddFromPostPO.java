package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.ThreadEntity.AddFromPostInput;

public class ThreadAddFromPostPO {
	
	private WebTarget mService;

	public ThreadAddFromPostPO(WebTarget service) {
		mService = service;
	}

  public Response addThreadFromPost(String subject, long postId, String authKey) {
    AddFromPostInput input = new AddFromPostInput();
    input.subject = (subject);
    input.postId = (postId);

    Response returnData = mService
        .path("rest").path("thread_frompost")
        .request().header("AuthKey", authKey)
        .put(Entity.entity(input, MediaType.APPLICATION_JSON));
    return returnData;
  }	

}

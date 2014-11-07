package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.serv.ThreadFromPostRequest.AddThreadFromPostResourceInput;

public class AddThreadFromPostPO {
	
	private WebTarget mService;

	public AddThreadFromPostPO(WebTarget service) {
		mService = service;
	}

  public Response addThreadFromPost(String subject, long postId, String authKey) {
    AddThreadFromPostResourceInput input = new AddThreadFromPostResourceInput();
    input.subject = (subject);
    input.postId = (postId);

    Response returnData = mService
        .path("rest").path("thread").path("frompost")
        .request().header("AuthKey", authKey)
        .put(Entity.entity(input, MediaType.APPLICATION_JSON));
    return returnData;
  }	

}

package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.push.PushResource;

import com.sun.jersey.api.client.WebResource;

public class PushIdsPO {
	
	private WebResource mService;

	public PushIdsPO(WebResource service) {
		mService = service;
	}

	public void add(String id) {
        	mService
        	.path("rest").path("push").path("add")
        	.type(MediaType.APPLICATION_JSON)
        	.put(id);
		return;
	}	

	public PushResource list() {
       return mService
        	.path("rest").path("push")
        	.get(PushResource.class);
	}	
	

}

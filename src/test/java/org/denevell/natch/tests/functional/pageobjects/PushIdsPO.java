package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.push.PushInput;
import org.denevell.natch.serv.push.PushResource;

public class PushIdsPO {
	
	private WebTarget mService;

	public PushIdsPO(WebTarget service) {
		mService = service;
	}

	public void add(String id) {
		PushInput entity = new PushInput();
		entity.setId(id);
        	mService
        	.path("rest").path("push").path("add").request()
        	.put(Entity.json(entity));
		return;
	}	

	public PushResource list() {
       return mService
        	.path("rest").path("push").request()
        	.get(PushResource.class);
	}	
	

}

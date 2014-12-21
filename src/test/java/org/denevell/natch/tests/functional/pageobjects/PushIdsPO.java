package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.entities.PushEntity.AddInput;
import org.denevell.natch.entities.PushEntity.Output;

public class PushIdsPO {
	
	private WebTarget mService;

	public PushIdsPO(WebTarget service) {
		mService = service;
	}

	public void add(String id) {
		AddInput entity = new AddInput();
		entity.id = id;
    mService
		.path("rest").path("add")
		  .path("PushEntity%24AddInput")
		  .request()
		  .accept(MediaType.APPLICATION_JSON)
		  .put(Entity.json(entity));
	}	

	public Output list() {
    return mService
		.path("rest")
		.path("list")
		.path("PushEntity")
		.request()
		.accept(MediaType.APPLICATION_JSON)
		.get(Output.class);
	}	
	

}

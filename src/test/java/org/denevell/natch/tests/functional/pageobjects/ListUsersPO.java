package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.users.UserList;

public class ListUsersPO {

	private WebTarget mService;

	public ListUsersPO(WebTarget service) {
		mService = service;
	}

	public UserList listUsers(String authKey) {
        return mService 
        .path("rest").path("user").path("list").request()
        .header("AuthKey", authKey)
        .get(UserList.class);
	}

}
package org.denevell.natch.tests.functional.pageobjects;

import java.util.List;

import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.users.User;
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
	
	public User findUser(String name, String authKey) {
		List<User> users = listUsers(authKey).getUsers();
		for (User user : users) {
			if(user.getUsername().equals(name)) {
				return user;
			}
		}
		return null;
	}
}
package org.denevell.natch.tests.functional;

import java.io.IOException;

import org.junit.Before;

import com.sun.jersey.api.client.WebResource;

public class LogoutFunctional {
	
	private WebResource service;

	@Before
	public void setup() throws IOException, InterruptedException {
		service = TestUtils.getRESTClient();
		service
	    	.path("rest")
	    	.path("register")
	    	.delete();
	}
	
	public void logout_shouldLogout() {
		
	}
	
	public void logout_shouldSeeErrorJsonIfNotLoggedIn() {
		
	}
	
}

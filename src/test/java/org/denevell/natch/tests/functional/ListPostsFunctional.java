package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.login.LoginResourceInput;
import org.denevell.natch.serv.login.LoginResourceReturnData;
import org.denevell.natch.serv.posts.ListPostsResource;
import org.denevell.natch.serv.register.RegisterResourceInput;
import org.denevell.natch.serv.register.RegisterResourceReturnData;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class ListPostsFunctional {
	
	private LoginResourceReturnData loginResult;
	private WebResource service;

	@Before
	public void setup() {
		service = TestUtils.getRESTClient();
		// Delete all users
		service
	    	.path("rest")
	    	.path("register")
	    	.delete();	
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    // Register
		service
	    	.path("rest").path("register").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);		
	}
	
	@Test
	public void shouldListByModificationDate() {
		// Arrange 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 
		
		// Assert
		assertTrue(returnData.getPosts().size()>0);
		assertTrue(returnData.getPosts().get(0).getId()!=0);
	}
	
	@Test
	public void shouldListByThreadId() {
	}
	
	@Test
	public void shouldListPostsInThread() {
	}
}

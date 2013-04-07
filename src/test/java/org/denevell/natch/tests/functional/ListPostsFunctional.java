package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.login.LoginResourceInput;
import org.denevell.natch.serv.login.LoginResourceReturnData;
import org.denevell.natch.serv.posts.resources.AddPostResourceInput;
import org.denevell.natch.serv.posts.resources.AddPostResourceReturnData;
import org.denevell.natch.serv.posts.resources.ListPostsResource;
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
	    	.path("testutils")
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
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2");
		service
		.path("rest").path("post")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		service
		.path("rest").path("post")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input1); 
		service
		.path("rest").path("post")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input2); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals(3, returnData.getPosts().size());
		assertTrue(returnData.getPosts().get(0).getId()!=0);
		assertTrue(returnData.getPosts().get(1).getId()!=0);
		assertTrue(returnData.getPosts().get(2).getId()!=0);
		assertEquals("sub", returnData.getPosts().get(0).getSubject());
		assertEquals("cont", returnData.getPosts().get(0).getContent());
		assertEquals("sub1", returnData.getPosts().get(1).getSubject());
		assertEquals("cont1", returnData.getPosts().get(1).getContent());
		assertEquals("sub2", returnData.getPosts().get(2).getSubject());
		assertEquals("cont2", returnData.getPosts().get(2).getContent());
	}
	
	@Test
	public void shouldListByThreadId() {
	}
	
	@Test
	public void shouldListPostsInThread() {
	}
}

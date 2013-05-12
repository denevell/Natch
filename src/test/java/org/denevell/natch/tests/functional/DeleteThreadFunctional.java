package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.DeletePostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class DeleteThreadFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;

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
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);				
	}
	
	@Test
	public void shouldDeleteThreadWhenNoRootPostButOtherPosts() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		input.setThreadId("newthread");
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1");
		input1.setThreadId("newthread");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input1); 
		ListPostsResource listThreads = service
		.path("rest").path("post").path("threads").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 		
		
		// Act
		DeletePostResourceReturnData ret = service.path("rest").path("post").path("del")
		.path(String.valueOf(listThreads.getPosts().get(0).getId()))
		.header("AuthKey", loginResult.getAuthKey())
		.entity(null)
		.delete(DeletePostResourceReturnData.class);
		ListPostsResource listThreadsAfter = service
		.path("rest").path("post").path("threads").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 		
		
		// Assert
		assertEquals("", ret.getError());
		assertTrue(ret.isSuccessful());		
		assertEquals(1, listThreads.getPosts().size());		
		assertEquals(0, listThreadsAfter.getPosts().size());		
	}
	
}

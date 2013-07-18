package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class ListSinglePostFunctional {
	
	private LoginResourceReturnData loginResult;
	private WebResource service;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		// Delete all users
		TestUtils.deleteTestDb();
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
	public void shouldListSinglePost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		// Arrange list posts 
		ListPostsResource postsList = service
		.path("rest").path("post").path("0").path("10")
    	.get(ListPostsResource.class); 	
		
		// Act
		PostResource returnData = service
		.path("rest").path("post").path(postsList.getPosts().get(0).getId()+"")
    	.get(PostResource.class); 
		
		// Assert
		assertEquals("Get subject of post", "sub", returnData.getSubject());
		assertEquals("Get content of post", "cont", returnData.getContent());
		assertNotNull("Get id of post", returnData.getId());
		assertNotNull("Get threadid of post", returnData.getThreadId());
	}
	
	@Test
	public void shouldShow404OnNoPost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		// Arrange list posts 
		ListPostsResource postsList = service
		.path("rest").path("post").path("0").path("10")
    	.get(ListPostsResource.class); 	
		
		// Act
		try {
			service
			.path("rest").path("post").path(postsList.getPosts().get(0).getId()+"blarblar")
	    	.get(PostResource.class); 		
		} catch (UniformInterfaceException e) {
			assertEquals(404, e.getResponse().getStatus());
			return;
		} catch(Exception e) {
			assertTrue("Expected 404", false);
			return;
		}

		// Assert
		assertTrue("Expected 404", false);
	}	
	
}

package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.junit.Before;
import org.junit.Test;

public class ListSinglePostFunctional {
	
	private LoginResourceReturnData loginResult;
	private WebTarget service;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		// Delete all users
		TestUtils.deleteTestDb();
	    new RegisterPO().register("aaron@aaron.com", "passy");
		// Login
		loginResult = new LoginPO().login("aaron@aaron.com", "passy");
	}
	
	@Test
	public void shouldListSinglePost() {
		// Arrange 
		// Add thread
		AddPostResourceInput input = new AddPostResourceInput("subthread", "contthread");
		AddPostResourceReturnData newThreads = AddThreadFunctional.addThread(service, loginResult.getAuthKey(), input);
		// Add post
		input = new AddPostResourceInput("subpost", "contpost");
		input.setThreadId(newThreads.getThread().getId());
		service
		.path("rest").path("post").path("add").request()
		.header("AuthKey", loginResult.getAuthKey())
    	.put(Entity.entity(input, MediaType.APPLICATION_JSON), AddPostResourceReturnData.class); 
		// Arrange list posts 
		ListPostsResource postsList = service
		.path("rest").path("post").path("0").path("10").request()
    	.get(ListPostsResource.class); 	
		
		// Act
		PostResource returnData = service
		.path("rest").path("post").path("single").path(postsList.getPosts().get(0).getId()+"").request()
    	.get(PostResource.class); 
		
		// Assert
		assertEquals("Get subject of post", "subthread", returnData.getSubject());
		assertEquals("Get content of post", "contpost", returnData.getContent());
		assertNotNull("Get id of post", returnData.getId());
		assertNotNull("Get threadid of post", returnData.getThreadId());
	}
	
	@Test
	public void shouldShow404OnNoPost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		AddThreadFunctional.addThread(service, loginResult.getAuthKey(), input);
		// Arrange list posts 
		ListPostsResource postsList = service
		.path("rest").path("post").path("0").path("10").request()
    	.get(ListPostsResource.class); 	
		
		// Act
		try {
			service
			.path("rest").path("post").path("single")
			.path(postsList.getPosts().get(0).getId()+"blarblar").request()
	    	.get(PostResource.class); 		
		} catch (WebApplicationException e) {
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

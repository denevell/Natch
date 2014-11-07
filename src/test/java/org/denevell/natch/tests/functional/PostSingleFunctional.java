package org.denevell.natch.tests.functional;

import javax.ws.rs.client.WebTarget;

import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;

public class PostSingleFunctional {
	
	private LoginResourceReturnData loginResult;
	private WebTarget service;

	/*
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
		AddPostResourceReturnData newThreads = ThreadAddFunctional.addThread(service, loginResult.getAuthKey(), input);
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
		assertNotNull("Get threadid of post", returnData.threadId);
	}
	
	@Test
	public void shouldShow404OnNoPost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		ThreadAddFunctional.addThread(service, loginResult.getAuthKey(), input);
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
	*/
	
}

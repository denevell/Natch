package org.denevell.natch.tests.functional;

import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;

public class ThreadListFunctional {
	
	private LoginResourceReturnData loginResult;
	private PostAddPO addPostPo;
  private PostsListPO postsListPo;

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	  addPostPo = new PostAddPO();
	  postsListPo = new PostsListPO();
	}
	
	/*
	
	@Test
	public void shouldPostsListByThreadId() {
		// Arrange 
		addPostPo.add("sub", "cont", loginResult.getAuthKey(), "t");
		addPostPo.add("sub1", "cont1", loginResult.getAuthKey(), "other");
		addPostPo.add("sub2", "cont2", loginResult.getAuthKey(), "t");
		
		// Act
		ThreadResource returnData = listThread.path("t").path("0").path("20").request()
    	.get(ThreadResource.class); 
		
		// Assert
		assertEquals(2, returnData.getPosts().size());
		assertTrue(returnData.getPosts().get(0).getId()!=0);
		assertTrue(returnData.getPosts().get(1).getId()!=0);
		assertEquals("sub", returnData.getSubject());
		assertEquals("aaron@aaron.com", returnData.getAuthor());
		assertEquals("cont", returnData.getPosts().get(0).getContent());
		assertEquals("t", returnData.getPosts().get(0).threadId);
		assertEquals("sub", returnData.getPosts().get(1).getSubject());
		assertEquals("cont2", returnData.getPosts().get(1).getContent());		
		assertEquals("t", returnData.getPosts().get(1).threadId);
	}
	
	@Test
	public void shouldListPostsByThreadIdWithLimit() {
		// Arrange 
		addPostPo.add("sub", "cont", new String[] {"again", "blar"}, loginResult.getAuthKey(), "t");
		addPostPo.add("sub1", "cont1", loginResult.getAuthKey(), "other");
		addPostPo.add("rubbish", "cont2", loginResult.getAuthKey(), "t");
		
		// Act
		ThreadResource returnData = listThread.path("t").path("1").path("1").request()
    	.get(ThreadResource.class); 
		
		// Assert
		assertEquals(1, returnData.getPosts().size());
		assertEquals(2, returnData.getNumPosts());
		assertEquals("again", returnData.getTags().get(0));
		assertEquals("sub", returnData.getSubject());
		assertEquals("aaron@aaron.com", returnData.getAuthor());
		assertEquals("cont2", returnData.getPosts().get(0).getContent());
		assertEquals("t", returnData.getPosts().get(0).threadId);
	}		
	
	@Test
	public void shouldShowBlankOnBadThreadId() {
		// Arrange 
		
		// Act
		try {
			service
			.path("rest").path("post").path("xxxxxxxxxxx").path("0").path("20").request()
	    	.get(ThreadResource.class); 
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

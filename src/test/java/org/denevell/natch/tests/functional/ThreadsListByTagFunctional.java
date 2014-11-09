package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.denevell.natch.model.PostEntity.Output;
import org.denevell.natch.model.ThreadEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostDeletePO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;


public class ThreadsListByTagFunctional {
	
	private LoginResourceReturnData loginResult;
  private ThreadAddPO threadAddPo;
  private ThreadsListPO threadsListPo;
  private PostAddPO postAddPo;
  private PostDeletePO postDeletePo;
  private PostsListPO postListPo;

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		threadAddPo = new ThreadAddPO();
		postAddPo = new PostAddPO();
		postDeletePo = new PostDeletePO();
		postListPo = new PostsListPO();
		threadsListPo = new ThreadsListPO();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	}

	/*
	@Test
	public void shouldListThreadsWithThreadWithLastModifiedContentFirst() {	
		threadAddPo("sub1", "cont1", "other", loginResult.getAuthKey());
		threadAddPo("sub1", "cont1", "t", loginResult.getAuthKey());

		// Note: input2 was added last so should be first in the list
		// Arrange - list them
		ListPostsResource listedPosts = service
		.path("rest").path("post").path("0").path("10").request()
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "sub2", listedPosts.getPosts().get(0).getSubject());
		
		// Act - modify input 1
		EditThreadResource editedInput = new EditThreadResource();
		editedInput.setContent("blar");
		editedInput.setSubject("blar2");
		EditPostResourceReturnData editReturnData = service
		.path("rest").path("post").path("editthread")
		.path(String.valueOf(listedPosts.getPosts().get(1).getId())).request()
		.header("AuthKey", loginResult.getAuthKey())
    	.post(Entity.json(editedInput), EditPostResourceReturnData.class); 			
		assertTrue(editReturnData.isSuccessful());
		
		// Assert - we've now got input1 first
		ListThreadsResource listedThreads = service
		.path("rest").path("threads").path("0").path("10").request()
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListThreadsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "blar2", listedThreads.getThreads().get(0).getSubject());
	}

	@Test
	public void shouldListThreadsByTag() {
		threadAddPo("x", "x", "x", new ArrayList<String>(){{add("onetag");}}, loginResult.getAuthKey());
		threadAddPo("sub", "cont", "t", new ArrayList<String>(){{add("onetag");}}, loginResult.getAuthKey());
		threadAddPo("sub1", "cont1", "other", loginResult.getAuthKey());
		threadAddPo("sub2", "cont2", "t", loginResult.getAuthKey());
		
		// Act
		ListThreadsResource returnData = service
		.path("rest").path("threads").path("onetag").path("0").path("10").request()
    	.get(ListThreadsResource.class); 
		
		// Assert
		assertEquals(2, returnData.getNumOfThreads());
		assertEquals(2, returnData.getThreads().size());
		assertEquals("sub", returnData.getThreads().get(0).getSubject());
		assertEquals("t", returnData.getThreads().get(0).getId());
		assertEquals("onetag", returnData.getThreads().get(0).getTags().get(1));
		
		assertEquals("x", returnData.getThreads().get(1).getSubject());
		assertEquals("x", returnData.getThreads().get(1).getId());
		assertEquals("onetag", returnData.getThreads().get(1).getTags().get(0));
	}	
	
	@Test
	public void shouldListThreadsByTagWithLimit() {
		threadAddPo("x", "x", "x", new ArrayList<String>(){{add("onetag");}}, loginResult.getAuthKey());
		threadAddPo("sub", "cont", "t", loginResult.getAuthKey());
		threadAddPo("sub1", "cont1", "other", loginResult.getAuthKey());
		threadAddPo("sub2", "cont2", "t", loginResult.getAuthKey());
		
		// Act
		ListThreadsResource returnData = service
		.path("rest").path("threads").path("onetag").path("1").path("1").request()
    	.get(ListThreadsResource.class); 
		
		// Assert
		assertEquals(2, returnData.getNumOfThreads());
		assertEquals(1, returnData.getThreads().size());
		assertEquals("x", returnData.getThreads().get(0).getSubject());
		assertEquals("x", returnData.getThreads().get(0).getId());
		assertEquals("onetag", returnData.getThreads().get(0).getTags().get(0));
	}
	
	@Ignore // Until we somehow get the edit ids when listing a post
	@SuppressWarnings("serial")
	@Test
	public void shouldListThreadsByTagWithThreadWithLastModifiedContentFirst() {	
		threadAddPo("sub1", "cont1", "other", new ArrayList<String>(){{add("a");add("b");}}, loginResult.getAuthKey());
		threadAddPo("sub2", "cont2", "t", new ArrayList<String>(){{add("a");add("b");}}, loginResult.getAuthKey());

		// Note: input2 was added last so should be first in the list
		// Arrange - list them
		ListThreadsResource listedPosts = service
		.path("rest").path("threads").path("a").path("0").path("10").request()
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListThreadsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "sub2", listedPosts.getThreads().get(0).getSubject());
		
		// Act - modify input 1
		EditThreadResource editedInput = new EditThreadResource();
		editedInput.setContent("blar");
		editedInput.setSubject("blar2");
		editedInput.setTags(new ArrayList<String>(){{add("a");add("b");}});
		EditPostResourceReturnData editReturnData = service
		.path("rest").path("post").path("editthread")
		.path(String.valueOf(listedPosts.getThreads().get(1).getId())).request()
		.header("AuthKey", loginResult.getAuthKey())
    	.post(Entity.json(editedInput), EditPostResourceReturnData.class) ; 			
		assertTrue(editReturnData.isSuccessful());
		
		// Assert - we've now got input1 first
		listedPosts = service
		.path("rest").path("threads").path("a").path("0").path("10").request()
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListThreadsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "blar2", listedPosts.getThreads().get(0).getSubject());
	}

	*/
}

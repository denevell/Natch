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


public class ThreadsListFunctional {
	
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

	@Test
	public void shouldListThreadsByPostLastEntered() {
		threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey());
		threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey());
		assertEquals(200, postAddPo.add("cont2", loginResult.getAuthKey(), "t").getStatus());
		
		OutputList returnData = threadsListPo.list(0, 10);
		
		// Assert
		assertEquals(2, returnData.numOfThreads);
		assertEquals(2, returnData.threads.size());
		assertEquals("sub", returnData.threads.get(0).subject);
		assertEquals("sub1", returnData.threads.get(1).subject);
		assertEquals("other", returnData.threads.get(1).id);		
		assertEquals("t", returnData.threads.get(0).id);		
	}

	@Test
	public void shouldHaveRootPostIdInThread() {
		threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey());
		threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey());
		
		OutputList returnData = threadsListPo.list(0, 10);
		
		long rootPostId1 = returnData.threads.get(0).rootPostId;
    assertTrue("Thread has root post", rootPostId1>0);
		long rootPostId2 = returnData.threads.get(1).rootPostId;
    assertTrue("Thread has root post", rootPostId2>0);
		assertTrue("Root posts aren't the same", rootPostId1!=rootPostId2);
	}

	@Test
	public void shouldListThreadsAfterThreadDelete() {
		threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey());
		threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey());
		threadAddPo.add("sub2", "cont2", "t", loginResult.getAuthKey());
		
		OutputList returnData = threadsListPo.list(0, 10);
		postDeletePo.delete(returnData.threads.get(0).rootPostId, loginResult.getAuthKey());
		returnData = threadsListPo.list(0, 10);
		
		assertEquals(1, returnData.numOfThreads);
		assertEquals(1, returnData.threads.size());
		assertEquals("other", returnData.threads.get(0).id);
	}
	
	@Test
	public void shouldListThreadsWithModificationDateAsLatestPost() {
		threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey());
		threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey());
		
		OutputList threads = threadsListPo.list(0, 10);
		Output post = postListPo.list("0", "10").posts.get(0);
		
    assertEquals("Has latest post's modificaton date", 
				threads.threads.get(0).modification, 
				post.modification);

		assertNotEquals("Doesn't have latest post's modificaton date", 
				threads.threads.get(1).modification, 
				post.modification);
	}
	
	@Test
	public void shouldListThreadsByPostLastEnteredWithLimit() {
		threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey());
		threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey());
		threadAddPo.add("sub2", "cont2", "t", loginResult.getAuthKey());
		
		OutputList threads = threadsListPo.list(1, 1);

		assertEquals(2, threads.numOfThreads);
		assertEquals(1, threads.threads.size());
		assertEquals("sub1", threads.threads.get(0).subject);
		assertEquals("other", threads.threads.get(0).id);
	}	

}

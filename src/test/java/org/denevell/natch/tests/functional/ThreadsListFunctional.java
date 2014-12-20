package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.entities.PostEntity.Output;
import org.denevell.natch.entities.ThreadEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadDeletePO;
import org.denevell.natch.tests.functional.pageobjects.ThreadEditPO;
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
  private ThreadDeletePO threadDeletePo;
  private PostsListPO postListPo;
  private ThreadEditPO threadEditPo;

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		threadAddPo = new ThreadAddPO();
		postAddPo = new PostAddPO();
		threadDeletePo = new ThreadDeletePO();
		postListPo = new PostsListPO();
		threadsListPo = new ThreadsListPO();
		threadEditPo = new ThreadEditPO();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	}

	@Test
	public void shouldListThreadsByPostLastEntered() {
		assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		assertEquals(200, postAddPo.add("cont2", "t", loginResult.getAuthKey()).getStatus());
		
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
		assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		
		OutputList returnData = threadsListPo.list(0, 10);
		
		long rootPostId1 = returnData.threads.get(0).rootPostId;
    assertTrue("Thread has root post", rootPostId1>0);
		long rootPostId2 = returnData.threads.get(1).rootPostId;
    assertTrue("Thread has root post", rootPostId2>0);
		assertTrue("Root posts aren't the same", rootPostId1!=rootPostId2);
	}

	@Test
	public void shouldListThreadsAfterThreadDelete() {
		assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		
		OutputList returnData = threadsListPo.list(0, 10);
    threadDeletePo.delete("t", loginResult.getAuthKey());

		returnData = threadsListPo.list(0, 10);
		assertEquals(1, returnData.numOfThreads);
		assertEquals(1, returnData.threads.size());
		assertEquals("other", returnData.threads.get(0).id);
	}
	
	@Test
	public void shouldListThreadsWithModificationDateAsLatestPost() {
		assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		
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
		assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		assertEquals(200, postAddPo.add("cont2", "t", loginResult.getAuthKey()).getStatus());
		
		OutputList threads = threadsListPo.list(1, 1);

		assertEquals(2, threads.numOfThreads);
		assertEquals(1, threads.threads.size());
		assertEquals("sub1", threads.threads.get(0).subject);
		assertEquals("other", threads.threads.get(0).id);
	}	

	@Test
	public void shouldListThreadsWithThreadWithLastModifiedContentFirst() {	
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub2", "cont2", "t", loginResult.getAuthKey()).getStatus());
		org.denevell.natch.entities.PostEntity.OutputList posts = postListPo.list("0", "10");
		assertEquals("Listing by last added", "sub2", posts.posts.get(0).subject);
		
		assertEquals(200, 
		    threadEditPo.edit("blar", "blar2", 
		        posts.posts.get(1).id, loginResult.getAuthKey()).getStatus());
		OutputList threads = threadsListPo.list(0, 10);
		
		assertEquals("Listing by last added", "blar", threads.threads.get(0).subject);
		assertEquals("Listing by last added", "sub2", threads.threads.get(1).subject);
	}

	@Test
	public void shouldHtmlEscapeSubjectContentTags() {
		assertEquals(200, threadAddPo.add("<hi>", "cont1", "t", Lists.newArrayList("<there>"), loginResult.getAuthKey()).getStatus());

		OutputList threads = threadsListPo.list(0, 10);

		assertEquals("&lt;hi&gt;", threads.threads.get(0).subject);
		assertEquals("&lt;there&gt;", threads.threads.get(0).tags.get(0));
	}		

}

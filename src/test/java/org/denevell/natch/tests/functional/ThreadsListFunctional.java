package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.entities.PostEntity.Output;
import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.gen.ServList.OutputWithCount;
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
		
		OutputWithCount<ThreadEntity.Output> returnData = threadsListPo.list(0, 10);
		
		// Assert
		assertEquals(2, returnData.count);
		assertEquals(2, returnData.results.size());
		assertEquals("sub", returnData.results.get(0).subject);
		assertEquals("sub1", returnData.results.get(1).subject);
		assertEquals("other", returnData.results.get(1).id);		
		assertEquals("t", returnData.results.get(0).id);		
	}

	@Test
	public void shouldHaveRootPostIdInThread() {
		assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		
		OutputWithCount<ThreadEntity.Output> returnData = threadsListPo.list(0, 10);
		
		PostEntity.Output rootPost1 = returnData.results.get(0).rootPost;
    assertTrue("Thread has root post", rootPost1!=null);
		PostEntity.Output rootPost2 = returnData.results.get(1).rootPost;
    assertTrue("Thread has root post", rootPost2!=null);
		assertTrue("Root posts aren't the same", rootPost1.id!=rootPost2.id);
	}

	@Test
	public void shouldListThreadsAfterThreadDelete() {
		assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		
		OutputWithCount<ThreadEntity.Output> returnData = threadsListPo.list(0, 10);
    threadDeletePo.delete("t", loginResult.getAuthKey());

		returnData = threadsListPo.list(0, 10);
		assertEquals(1, returnData.count);
		assertEquals(1, returnData.results.size());
		assertEquals("other", returnData.results.get(0).id);
	}
	
	@Test
	public void shouldListThreadsWithModificationDateAsLatestPost() {
		assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		
		OutputWithCount<ThreadEntity.Output> threads = threadsListPo.list(0, 10);
		Output post = postListPo.list("0", "10").get(0);
		
    assertEquals("Has latest post's modificaton date", 
				threads.results.get(0).modification, 
				post.modification);

		assertNotEquals("Doesn't have latest post's modificaton date", 
				threads.results.get(1).modification, 
				post.modification);
	}
	
	@Test
	public void shouldListThreadsByPostLastEnteredWithLimit() {
		assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		assertEquals(200, postAddPo.add("cont2", "t", loginResult.getAuthKey()).getStatus());
		
		OutputWithCount<ThreadEntity.Output> threads = threadsListPo.list(1, 1);

		assertEquals(2, threads.count);
		assertEquals(1, threads.results.size());
		assertEquals("sub1", threads.results.get(0).subject);
		assertEquals("other", threads.results.get(0).id);
	}	

	@Test
	public void shouldListThreadsWithThreadWithLastModifiedContentFirst() {	
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub2", "cont2", "t", loginResult.getAuthKey()).getStatus());
		org.denevell.natch.entities.PostEntity.OutputList posts = postListPo.list("0", "10");
		assertEquals("Listing by last added", "sub2", posts.get(0).subject);
		
		assertEquals(200, 
		    threadEditPo.edit("blar", "blar2", 
		        posts.get(1).id, loginResult.getAuthKey()).getStatus());
		OutputWithCount<ThreadEntity.Output> threads = threadsListPo.list(0, 10);
		
		assertEquals("Listing by last added", "blar", threads.results.get(0).subject);
		assertEquals("Listing by last added", "sub2", threads.results.get(1).subject);
	}

	@Test
	public void shouldHtmlEscapeSubjectContentTags() {
		assertEquals(200, threadAddPo.add("<hi>", "cont1", "t", Lists.newArrayList("<there>"), loginResult.getAuthKey()).getStatus());

		OutputWithCount<ThreadEntity.Output> threads = threadsListPo.list(0, 10);

		assertEquals("&lt;hi&gt;", threads.results.get(0).subject);
		assertEquals("&lt;there&gt;", threads.results.get(0).tags.get(0));
	}		

}

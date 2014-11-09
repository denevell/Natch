package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.denevell.natch.model.ThreadEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostDeletePO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadEditPO;
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
  private ThreadEditPO threadEditPo;

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		threadAddPo = new ThreadAddPO();
		postAddPo = new PostAddPO();
		threadEditPo = new ThreadEditPO();
		postDeletePo = new PostDeletePO();
		postListPo = new PostsListPO();
		threadsListPo = new ThreadsListPO();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	}

	@SuppressWarnings("serial")
  @Test
	public void shouldListThreadsByTag() {
		threadAddPo.add("x", "x", "x", new ArrayList<String>(){{add("onetag");}}, loginResult.getAuthKey());
		threadAddPo.add("sub", "cont", "t", new ArrayList<String>(){{add("onetag");}}, loginResult.getAuthKey());
		threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey());
		threadAddPo.add("sub2", "cont2", "t", loginResult.getAuthKey());
		
		OutputList returnData= threadsListPo.byTag("onetag", 0, 10);
		
		assertEquals(2, returnData.numOfThreads);
		assertEquals(2, returnData.threads.size());
		assertEquals("sub", returnData.threads.get(0).subject);
		assertEquals("t", returnData.threads.get(0).id);
		assertEquals("onetag", returnData.threads.get(0).tags.get(0));
		
		assertEquals("x", returnData.threads.get(1).subject);
		assertEquals("x", returnData.threads.get(1).id);
		assertEquals("onetag", returnData.threads.get(1).tags.get(0));
	}	

	@SuppressWarnings("serial")
  @Test
	public void shouldListThreadsByTagWithLimit() {
		threadAddPo.add("x", "x", "x", new ArrayList<String>(){{add("onetag");}}, loginResult.getAuthKey());
		threadAddPo.add("sub", "cont", "t", new ArrayList<String>(){{add("onetag");}}, loginResult.getAuthKey());
		threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey());
		threadAddPo.add("sub2", "cont2", "t", loginResult.getAuthKey());
		
		// Act
		OutputList returnData= threadsListPo.byTag("onetag", 1, 1);
		
		// Assert
		assertEquals(2, returnData.numOfThreads);
		assertEquals(1, returnData.threads.size());
		assertEquals("x", returnData.threads.get(0).subject);
		assertEquals("x", returnData.threads.get(0).id);
		assertEquals("onetag", returnData.threads.get(0).tags.get(0));
	}
	/*
	
	@Ignore // Until we somehow get the edit ids when listing a post
	@SuppressWarnings("serial")
	@Test
	public void shouldListThreadsByTagWithThreadWithLastModifiedContentFirst() {	
		threadAddPo("sub1", "cont1", "other", new ArrayList<String>(){{add("a");add("b");}}, loginResult.getAuthKey());
		threadAddPo("sub2", "cont2", "t", new ArrayList<String>(){{add("a");add("b");}}, loginResult.getAuthKey());

		OutputList listedPosts = threadsListPo.byTag("a", 0, 10);
		assertEquals("Listing by last added", "sub2", listedPosts.getThreads().get(0).getSubject());
		
		assertEquals(200, 
		    threadEditPo.edit("blar", "blar2", 
		        listedPosts.threads.get(1).id, loginResult.getAuthKey()).getStatus());
		
		// Assert - we've now got input1 first
		listedPosts = threadsListPo.byTag("a", 0, 10);
		assertEquals("Listing by last added", "blar2", listedPosts.getThreads().get(0).getSubject());
	}

	*/
}

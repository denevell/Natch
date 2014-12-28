package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.gen.ServList.OutputWithCount;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
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
  private PostAddPO postAddPo = new PostAddPO();

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		threadAddPo = new ThreadAddPO();
		threadsListPo = new ThreadsListPO();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	}

  @Test
	public void shouldListThreadsByTag() {
		assertEquals(200, threadAddPo.add("x", "x", "x", Lists.newArrayList("onetag", "two"), loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub", "cont", "t", Lists.newArrayList("onetag", "two"), loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		assertEquals(200, postAddPo.add("cont2", "t", loginResult.getAuthKey()).getStatus());
		
		OutputWithCount<ThreadEntity.Output> returnData= threadsListPo.byTag("onetag", 0, 10);
		
		assertEquals(2, returnData.count);
		assertEquals(2, returnData.results.size());
		assertEquals("sub", returnData.results.get(0).subject);
		assertEquals("t", returnData.results.get(0).id);
		assertEquals("onetag", returnData.results.get(0).tags.get(0));
		
		assertEquals("x", returnData.results.get(1).subject);
		assertEquals("x", returnData.results.get(1).id);
		assertEquals("onetag", returnData.results.get(1).tags.get(0));
	}	

  @Test
	public void shouldListThreadsByTagWithLimit() {
		assertEquals(200, threadAddPo.add("x", "x", "x", Lists.newArrayList("onetag", "two"), loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub", "cont", "t", Lists.newArrayList("onetag", "two"), loginResult.getAuthKey()).getStatus());
		assertEquals(200, threadAddPo.add("sub1", "cont1", "other", loginResult.getAuthKey()).getStatus());
		assertEquals(200, postAddPo.add("cont2", "t", loginResult.getAuthKey()).getStatus());
		
		// Act
		OutputWithCount<ThreadEntity.Output> returnData= threadsListPo.byTag("onetag", 1, 1);
		
		// Assert
		assertEquals(2, returnData.count);
		assertEquals(1, returnData.results.size());
		assertEquals("x", returnData.results.get(0).subject);
		assertEquals("x", returnData.results.get(0).id);
		assertEquals("onetag", returnData.results.get(0).tags.get(0));
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

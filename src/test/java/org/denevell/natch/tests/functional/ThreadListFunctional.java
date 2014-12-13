package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.entities.ThreadEntity.Output;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;

public class ThreadListFunctional {
	
	private LoginResourceReturnData loginResult;
	private PostAddPO postAddPo;
  private ThreadAddPO threadAddPo;
  private ThreadsListPO threadsList;

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	  postAddPo = new PostAddPO();
		threadsList = new ThreadsListPO();
		threadAddPo = new ThreadAddPO();
		TestUtils.deleteTestDb();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	}
	
	
	@Test
	public void shouldPostsListByThreadId() {
	  assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
	  assertEquals(200, threadAddPo.add("sub", "cont", "other", Lists.newArrayList("tagy"), loginResult.getAuthKey()).getStatus());
	  assertEquals(200, postAddPo.add("cont1", loginResult.getAuthKey(), "other").getStatus());
	  assertEquals(200, postAddPo.add("cont2", loginResult.getAuthKey(), "t").getStatus());
		
		Output returnData = threadsList.byThread("t", 0, 10);
		
		assertEquals(2, returnData.posts.size());
		assertTrue(returnData.posts.get(0).id!=0);
		assertTrue(returnData.posts.get(1).id!=0);
		assertEquals("sub", returnData.subject);
		assertEquals("aaron@aaron.com", returnData.author);
		assertEquals("cont", returnData.posts.get(0).content);
		assertEquals("t", returnData.posts.get(0).threadId);
		assertEquals("cont2", returnData.posts.get(1).content);		
		assertEquals("t", returnData.posts.get(1).threadId);
	}
	
	@Test
	public void shouldListPostsByThreadIdWithLimit() {
	  assertEquals(200, threadAddPo.add("sub", "cont", "t", Lists.newArrayList("tagy"), loginResult.getAuthKey()).getStatus());
	  assertEquals(200, threadAddPo.add("sub", "cont", "other", Lists.newArrayList("tagy"), loginResult.getAuthKey()).getStatus());
	  //assertEquals(200, postAddPo.add("cont1", loginResult.getAuthKey(), "other").getStatus());
	  assertEquals(200, postAddPo.add("cont2", loginResult.getAuthKey(), "t").getStatus());

		Output returnData = threadsList.byThread("t", 1, 1);
		
		assertEquals(1, returnData.posts.size());
		assertEquals(2, returnData.numPosts);
		assertEquals("sub", returnData.subject);
		assertEquals("tagy", returnData.tags.get(0));
		assertEquals("aaron@aaron.com", returnData.author);
		assertEquals("cont2", returnData.posts.get(0).content);
		assertEquals("t", returnData.posts.get(0).threadId);
	}		
	
	@Test
	public void shouldShowBlankOnBadThreadId() {
	  assertEquals(200, threadAddPo.add("sub", "cont", "t", loginResult.getAuthKey()).getStatus());
	  assertEquals(200, postAddPo.add("cont2", loginResult.getAuthKey(), "t").getStatus());

		threadsList.byThreadShow404("rubbish", 1, 1);
	}	
	
	@Test
	public void shouldHtmlEscapeSubjectContentTags() {
	  assertEquals(200, threadAddPo.add("<hi>", "<there>", "t", Lists.newArrayList("<again>"), loginResult.getAuthKey()).getStatus());
		
		Output returnData = threadsList.byThread("t", 0, 10);

		assertEquals("&lt;hi&gt;", returnData.subject);
		assertEquals("&lt;there&gt;", returnData.posts.get(0).content);
		assertEquals("&lt;again&gt;", returnData.tags.get(0));
	}		

}

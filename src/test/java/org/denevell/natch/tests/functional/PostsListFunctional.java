package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;

public class PostsListFunctional {
	
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
	
	@Test
	public void shouldListByCreationDate() {
		addPostPo.add("cont", loginResult.getAuthKey(), "thread");
		addPostPo.add("cont1", loginResult.getAuthKey(), "thread");
		addPostPo.add("cont2", loginResult.getAuthKey(), "thread");
		
		OutputList returnData = postsListPo.list("0", "10");
		
		assertEquals(3, returnData.posts.size());
		assertTrue(returnData.posts.get(0).id!=0);
		assertTrue(returnData.posts.get(1).id!=0);
		assertTrue(returnData.posts.get(2).id!=0);
		assertEquals("cont2", returnData.posts.get(0).content);
		assertEquals("cont1", returnData.posts.get(1).content);
		assertEquals("cont", returnData.posts.get(2).content);
	}

	@Test
	public void shouldListByCreationDateWithLimit() {
		addPostPo.add("cont", loginResult.getAuthKey(), "thread");
		addPostPo.add("cont1", loginResult.getAuthKey(), "thread");
		addPostPo.add("cont2", loginResult.getAuthKey(), "thread");
		
		OutputList returnData = postsListPo.list("0", "1");
		
		assertEquals(1, returnData.posts.size());
		assertTrue(returnData.posts.get(0).id!=0);
		assertEquals("cont2", returnData.posts.get(0).content);
	}	
	
	@Test
	public void shouldHtmlEscapeSubjectContentTags() {
		addPostPo.add("<hi>", loginResult.getAuthKey(), "thread");
		
		OutputList returnData = postsListPo.list("0", "10");

		assertEquals("&lt;hi&gt;", returnData.posts.get(0).content);
	}		

}

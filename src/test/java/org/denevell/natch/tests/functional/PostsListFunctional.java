package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
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
  private ThreadAddPO threadAddPo;

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	  addPostPo = new PostAddPO();
	  threadAddPo = new ThreadAddPO();
	  postsListPo = new PostsListPO();
	  assertEquals(200, threadAddPo.add("sub", "cont_", "thread", loginResult.getAuthKey()).getStatus());
	}
	
	@Test
	public void shouldListByCreationDate() {
		assertEquals(200, addPostPo.add("cont", "thread", loginResult.getAuthKey()).getStatus());
		assertEquals(200, addPostPo.add("cont1", "thread", loginResult.getAuthKey()).getStatus());
		assertEquals(200, addPostPo.add("cont2", "thread", loginResult.getAuthKey()).getStatus());
		
		OutputList returnData = postsListPo.list("0", "10");
		
		assertEquals(4, returnData.size());
		assertTrue(returnData.get(0).id!=0);
		assertTrue(returnData.get(1).id!=0);
		assertTrue(returnData.get(2).id!=0);
		assertEquals("cont2", returnData.get(0).content);
		assertEquals("cont1", returnData.get(1).content);
		assertEquals("cont", returnData.get(2).content);
	}

	@Test
	public void shouldListByCreationDateWithLimit() {
		addPostPo.add("cont", "thread", loginResult.getAuthKey());
		addPostPo.add("cont1", "thread", loginResult.getAuthKey());
		addPostPo.add("cont2", "thread", loginResult.getAuthKey());
		
		OutputList returnData = postsListPo.list("0", "1");
		
		assertEquals(1, returnData.size());
		assertTrue(returnData.get(0).id!=0);
		assertEquals("cont2", returnData.get(0).content);
	}	
	
	@Test
	public void shouldHtmlEscapeSubjectContentTags() {
		addPostPo.add("<hi>", "thread", loginResult.getAuthKey());
		
		OutputList returnData = postsListPo.list("0", "10");

		assertEquals("&lt;hi&gt;", returnData.get(0).content);
	}		

}

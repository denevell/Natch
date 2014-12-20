package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.denevell.natch.entities.PostEntity.Output;
import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostSinglePO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;

public class PostSingleFunctional {
	
	private LoginResourceReturnData loginResult;
  private PostAddPO postAddPo;
  private PostsListPO postsListPo;
  private PostSinglePO postSinglePo;
  private ThreadAddPO threadAddPo = new ThreadAddPO();

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
		postAddPo = new PostAddPO();
		postsListPo = new PostsListPO();
		postSinglePo = new PostSinglePO();
	  assertEquals(200, threadAddPo.add("sub", "cont", "thread", loginResult.getAuthKey()).getStatus());
	}

	
	@Test
	public void shouldListSinglePost() {
		postAddPo.add("contxx", "thread", loginResult.getAuthKey());
		OutputList posts = postsListPo.list("0", "10");
		Output single = postSinglePo.single(posts.get(0).id);
		assertEquals("Get content of post", "contxx", single.content);
		assertEquals("Get subject of post", "sub", single.subject);
		assertEquals("thread", single.threadId);
		assertTrue("Has a id more than zero", single.id > 0);
	}

	@Test
	public void shouldShow404OnNoPost() {
		postSinglePo.gives404OnBadId();
	}	
	
}

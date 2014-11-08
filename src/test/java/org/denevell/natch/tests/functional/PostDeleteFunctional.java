package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.Response;

import org.denevell.natch.model.PostEntity.Output;
import org.denevell.natch.model.PostEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostDeletePO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;


public class PostDeleteFunctional {
	
	private LoginResourceReturnData loginResult;
	private UserRegisterPO registerPo;
  private PostAddPO postAddPo;
  private PostDeletePO postDeletePo;
  private String authKey;
  private PostsListPO postListPo;

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		postAddPo = new PostAddPO();
		postListPo = new PostsListPO();
		postDeletePo = new PostDeletePO();
	  registerPo = new UserRegisterPO();
	  registerPo.register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
		authKey = loginResult.getAuthKey();
	}
	
	@Test
	public void shouldDeletePost() {
		assertEquals(200, postAddPo.add("cont", authKey, "thread").getStatus());
		assertEquals(200, postAddPo.add("cont1", authKey, "thread").getStatus());
		List<Output> posts = postListPo.list("0", "10").posts;
		assertEquals(2, posts.size());
		postDeletePo.delete(posts.get(0).id, authKey);
		List<Output> postsAfter = postListPo.list("0", "10").posts;
		assertEquals(1, postsAfter.size());
		assertEquals("cont", postsAfter.get(0).content);
	}

	@Test
	public void shouldSeeErrorOnUnAuthorised() {
	  registerPo.register("other_user", "passy");
		String otherUserAuthKey = new UserLoginPO().login("other_user", "passy").getAuthKey();

		postAddPo.add("cont1", authKey, "thread").getStatus();
		OutputList posts = postListPo.list("0", "10");
		
		Response response = postDeletePo.delete(posts.posts.get(0).id, otherUserAuthKey);
		OutputList postsAfter = postListPo.list("0", "10");

		assertEquals(403, response.getStatus());
		assertEquals(1, posts.posts.size());
		assertEquals(1, postsAfter.posts.size());
	}

	@Test
	public void shouldSeeErrorOnUnknownPost() {
		postAddPo.add("cont1", authKey, "thread").getStatus();
		OutputList posts = postListPo.list("0", "10");
		
		Response response = postDeletePo.delete(posts.posts.get(0).id+999, authKey);
		OutputList postsAfter = postListPo.list("0", "10");

		assertEquals(404, response.getStatus());
		assertEquals(1, posts.posts.size());
		assertEquals(1, postsAfter.posts.size());
	}

	@Test
	public void shouldAllowAdminToDelete() {
	  registerPo.register("other_user", "passy");
		String otherUserAuthKey = new UserLoginPO().login("other_user", "passy").getAuthKey();

		postAddPo.add("cont1", otherUserAuthKey, "thread").getStatus();
		OutputList posts = postListPo.list("0", "10");
		
		Response response = postDeletePo.delete(posts.posts.get(0).id, authKey);
		OutputList postsAfter = postListPo.list("0", "10");

		assertEquals(200, response.getStatus());
		assertEquals(1, posts.posts.size());
		assertEquals(0, postsAfter.posts.size());
	}

}

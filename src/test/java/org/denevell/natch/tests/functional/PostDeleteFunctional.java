package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity.Output;
import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostDeletePO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
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
  private ThreadAddPO threadAddPo;

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		threadAddPo = new ThreadAddPO();
		postAddPo = new PostAddPO();
		postListPo = new PostsListPO();
		postDeletePo = new PostDeletePO();
	  registerPo = new UserRegisterPO();
	  registerPo.register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
		authKey = loginResult.getAuthKey();
	}

	// Test to not delete the only post in a thread?
	// Test deleting the only post in thread
	
	@Test
	public void shouldDeletePost() {
		assertEquals(200, threadAddPo.add("sub", "cont", "thread", authKey).getStatus());
		assertEquals(200, postAddPo.add("cont1", authKey, "thread").getStatus());
		List<Output> posts = postListPo.list("0", "10").posts;
		assertEquals(2, posts.size());
		Output post = posts.get(0);
    assertEquals(200, postDeletePo.delete(post.id, post.threadId, authKey).getStatus());
		List<Output> postsAfter = postListPo.list("0", "10").posts;
		assertEquals(1, postsAfter.size());
		assertEquals("cont", postsAfter.get(0).content);
	}
	
	// Test for incorrect thread id

	@Test
	public void shouldSeeErrorOnUnAuthorisedDelete() {
	  registerPo.register("other_user", "passy");
		String otherUserAuthKey = new UserLoginPO().login("other_user", "passy").getAuthKey();

		threadAddPo.add("sub", "cont1", "thread", authKey);
		OutputList posts = postListPo.list("0", "10");
		
		Output output = posts.posts.get(0);
    Response response = postDeletePo.delete(output.id, output.threadId, otherUserAuthKey);
		OutputList postsAfter = postListPo.list("0", "10");

		assertEquals(403, response.getStatus());
		assertEquals(1, posts.posts.size());
		assertEquals(1, postsAfter.posts.size());
	}

	@Test
	public void shouldSeeErrorOnUnknownPost() {
		threadAddPo.add("sub", "cont1", "thread", authKey).getStatus();
		OutputList posts = postListPo.list("0", "10");
		
		Output output = posts.posts.get(0);
    Response response = postDeletePo.delete(output.id+999, output.threadId, authKey);
		OutputList postsAfter = postListPo.list("0", "10");

		assertEquals(404, response.getStatus());
		assertEquals(1, posts.posts.size());
		assertEquals(1, postsAfter.posts.size());
	}

	@Test
	public void shouldAllowAdminToDelete() {
	  registerPo.register("other_user", "passy");
		String otherUserAuthKey = new UserLoginPO().login("other_user", "passy").getAuthKey();

		assertEquals(200, threadAddPo.add("sub", "cont1", "thread", otherUserAuthKey).getStatus());
		assertEquals(200, postAddPo.add("cont1", authKey, "thread").getStatus());
		OutputList posts = postListPo.list("0", "10");
		
		Output output = posts.posts.get(0);
    Response response = postDeletePo.delete(output.id, output.threadId, authKey);
		OutputList postsAfter = postListPo.list("0", "10");

		assertEquals(200, response.getStatus());
		assertEquals(2, posts.posts.size());
		assertEquals(1, postsAfter.posts.size());
	}

}

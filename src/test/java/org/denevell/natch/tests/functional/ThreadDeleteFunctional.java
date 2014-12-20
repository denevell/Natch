package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity.Output;
import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadDeletePO;
import org.denevell.natch.tests.functional.pageobjects.ThreadsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;


public class ThreadDeleteFunctional {
	
	private LoginResourceReturnData loginResult;
	private UserRegisterPO registerPo;
  private PostAddPO postAddPo;
  private String authKey;
  private PostsListPO postListPo;
  private ThreadAddPO threadAddPo;
  private ThreadDeletePO threadDeletePo;
  private ThreadsListPO threadsListPo;

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		threadAddPo = new ThreadAddPO();
		postAddPo = new PostAddPO();
		postListPo = new PostsListPO();
		threadDeletePo = new ThreadDeletePO();
		threadsListPo = new ThreadsListPO();
	  registerPo = new UserRegisterPO();
	  registerPo.register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
		authKey = loginResult.getAuthKey();
	}

	@Test
	public void shouldDeleteThread() {
		assertEquals(200, threadAddPo.add("sub", "cont", "thread", authKey).getStatus());
		assertEquals(1, threadsListPo.list(0, 10).threads.size());
		assertEquals(1, threadsListPo.list(0, 10).numOfThreads);
		assertEquals(1, postListPo.list("0", "10").posts.size());

    assertEquals(200, threadDeletePo.delete("thread", authKey).getStatus());

		assertEquals(0, threadsListPo.list(0, 10).threads.size());
		assertEquals(0, threadsListPo.list(0, 10).numOfThreads);
		assertEquals(0, postListPo.list("0", "10").posts.size());
	}

	@Test
	public void shouldDeleteThreadWhenOnePost() {
		assertEquals(200, threadAddPo.add("sub", "cont", "thread", authKey).getStatus());
		assertEquals(200, postAddPo.add("conttt", "thread", authKey).getStatus());
		assertEquals(1, threadsListPo.list(0, 10).threads.size());
		assertEquals(1, threadsListPo.list(0, 10).numOfThreads);
		assertEquals(2, postListPo.list("0", "10").posts.size());

    assertEquals(200, threadDeletePo.delete("thread", authKey).getStatus());

		assertEquals(0, threadsListPo.list(0, 10).threads.size());
		assertEquals(0, threadsListPo.list(0, 10).numOfThreads);
		assertEquals(0, postListPo.list("0", "10").posts.size());
	}
	
	@Test
	public void shouldSeeErrorOnUnAuthorisedDeleteThread() {
	  registerPo.register("other_user", "passy");
		String otherUserAuthKey = new UserLoginPO().login("other_user", "passy").getAuthKey();

		threadAddPo.add("sub", "cont1", "thread", authKey);
		OutputList posts = postListPo.list("0", "10");
		
		Output output = posts.posts.get(0);
    Response response = threadDeletePo.delete(output.threadId, otherUserAuthKey);
		OutputList postsAfter = postListPo.list("0", "10");

		assertEquals(403, response.getStatus());
		assertEquals(1, posts.posts.size());
		assertEquals(1, postsAfter.posts.size());
	}

	@Test
	public void shouldSeeErrorOnUnknownThread() {
		threadAddPo.add("sub", "cont1", "thread", authKey).getStatus();
		OutputList posts = postListPo.list("0", "10");
		
		Output output = posts.posts.get(0);
    Response response = threadDeletePo.delete(output.threadId+"BAD", authKey);
		OutputList postsAfter = postListPo.list("0", "10");

		assertEquals(404, response.getStatus());
		assertEquals(1, posts.posts.size());
		assertEquals(1, postsAfter.posts.size());
	}

	@Test
	public void shouldAllowAdminToDeleteThread() {
	  registerPo.register("other_user", "passy");
		String otherUserAuthKey = new UserLoginPO().login("other_user", "passy").getAuthKey();

		assertEquals(200, threadAddPo.add("sub", "cont1", "thread", otherUserAuthKey).getStatus());
		assertEquals(200, postAddPo.add("cont1", "thread", authKey).getStatus());
		OutputList posts = postListPo.list("0", "10");
		assertEquals(2, posts.posts.size());
		
		Output output = posts.posts.get(0);
    Response response = threadDeletePo.delete(output.threadId, authKey);
		OutputList postsAfter = postListPo.list("0", "10");

		assertEquals(200, response.getStatus());
		assertEquals(0, postsAfter.posts.size());
	}

}

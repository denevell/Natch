package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response;

import org.denevell.natch.model.PostEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddFromPostPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;


public class ThreadAddFromMovedPostFunctional {
	
	private LoginResourceReturnData adminLoginResult;
	private UserRegisterPO registerPo;
	private PostAddPO postAddPo;
	private UserLoginPO loginPo;
	private ThreadAddFromPostPO threadAddFromPostPo;
  private PostsListPO postsListPo;
  private ThreadsListPO threadsList;
  private ThreadAddPO threadAdd;
	
	@Before
	public void setup() throws Exception {
		registerPo = new UserRegisterPO();
		loginPo = new UserLoginPO();
		postAddPo = new PostAddPO();
		postsListPo = new PostsListPO();
		threadAddFromPostPo = new ThreadAddFromPostPO(TestUtils.getRESTClient());
		threadsList = new ThreadsListPO();
		threadAdd = new ThreadAddPO();
		TestUtils.deleteTestDb();
	  registerPo.register("aaron@aaron.com", "passy");
		adminLoginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	}
	
	@Test
	public void shouldMakeThreadFromPost() {
	  registerPo.register("other", "other");
		LoginResourceReturnData loginResult = loginPo.login("other", "other");

		threadAdd.add("New thread", "first post", loginResult.getAuthKey());
		org.denevell.natch.model.ThreadEntity.OutputList threads = threadsList.list(0, 10);
		postAddPo.add("Second post", loginResult.getAuthKey(), threads.threads.get(0).id);
		
		OutputList posts = postsListPo.list("0", "10");
		assertTrue("Should have two posts, thread starter and first post", posts.posts.size()==2);
		
		Response returnData = threadAddFromPostPo.addThreadFromPost(
    		"New subject",
    		posts.posts.get(0).id,
    		adminLoginResult.getAuthKey()); 
		
		assertEquals(200, returnData.getStatus());
		posts = postsListPo.list("0", "10");
		assertEquals("New thread new new subject", posts.posts.get(0).subject, "New subject");
		assertEquals("New thread has old user id", posts.posts.get(0).username, "other");
		assertTrue("New thread is marked edited by admin", posts.posts.get(0).adminEdited);
		assertTrue("Still just have two posts, since one's been moved", posts.posts.size()==2);
	}

	@Test
	public void shouldThrow403WhenNotAdmin() {
	  registerPo.register("other", "other");
		LoginResourceReturnData loginResult = loginPo.login("other", "other");

		threadAdd.add("New thread", "first post", loginResult.getAuthKey());
		org.denevell.natch.model.ThreadEntity.OutputList threads = threadsList.list(0, 10);
		postAddPo.add("Second post", loginResult.getAuthKey(), threads.threads.get(0).id);
		
		OutputList posts = postsListPo.list("0", "10");
		assertTrue("Should have two posts, thread starter and first post", posts.posts.size()==2);
		
		Response returnData = threadAddFromPostPo.addThreadFromPost(
    		"New subject",
    		posts.posts.get(0).id,
    		loginResult.getAuthKey()); 
		assertEquals(403, returnData.getStatus());

		threads = threadsList.list(0, 10);
		assertTrue("Should have one thread now", threads.numOfThreads==1);
	}
	
}

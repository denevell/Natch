package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.gen.ServList.OutputWithCount;
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


@SuppressWarnings("unchecked")
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

		assertEquals(200, threadAdd.add("New thread", "first post", loginResult.getAuthKey()).getStatus());
		OutputWithCount<ThreadEntity.Output> threads = threadsList.list(0, 10);
		assertEquals(200, postAddPo.add("Second post", threads.results.get(0).id, loginResult.getAuthKey()).getStatus());
		
		OutputList posts = postsListPo.list("0", "10");
		assertTrue("Should have two posts, thread starter and first post", posts.size()==2);
		
		Response returnData = threadAddFromPostPo.addThreadFromPost(
    		"New subject",
    		posts.get(0).id,
    		adminLoginResult.getAuthKey()); 

		assertEquals(200, returnData.getStatus());
    HashMap<String, String> hm = (HashMap<String, String>) returnData.readEntity(HashMap.class);
		assertTrue("Returns the new thread id", hm.get("threadId")!=null);
		posts = postsListPo.list("0", "10");
		assertEquals("New thread new new subject", posts.get(0).subject, "New subject");
		assertEquals("New thread has old user id", posts.get(0).username, "other");
		assertTrue("New thread is marked edited by admin", posts.get(0).adminEdited);
		assertTrue("Still just have two posts, since one's been moved", posts.size()==2);
	}

	@Test
	public void shouldThrow403WhenNotAdmin() {
	  registerPo.register("other", "other");
		LoginResourceReturnData loginResult = loginPo.login("other", "other");

		threadAdd.add("New thread", "first post", loginResult.getAuthKey());
		OutputWithCount<ThreadEntity.Output> threads = threadsList.list(0, 10);
		postAddPo.add("Second post", threads.results.get(0).id, loginResult.getAuthKey());
		
		OutputList posts = postsListPo.list("0", "10");
		assertTrue("Should have two posts, thread starter and first post", posts.size()==2);
		
		Response returnData = threadAddFromPostPo.addThreadFromPost(
    		"New subject",
    		posts.get(0).id,
    		loginResult.getAuthKey()); 
		assertEquals(403, returnData.getStatus());

		threads = threadsList.list(0, 10);
		assertTrue("Should have one thread now", threads.count==1);
	}
	
}

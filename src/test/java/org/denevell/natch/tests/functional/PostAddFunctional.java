package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity.Output;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;

public class PostAddFunctional {
	
	private LoginResourceReturnData loginResult;
  private String authKey;
	private PostAddPO postAddPo;
  private PostsListPO postListPo;
  private ThreadAddPO threadAddPo;
	
	@Before
	public void setup() throws Exception {
		postAddPo = new PostAddPO();
		postListPo = new PostsListPO();
		threadAddPo = new ThreadAddPO();
		TestUtils.deleteTestDb();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
		authKey = loginResult.getAuthKey();
	  threadAddPo.add("sub", "cont", "thread", authKey);
	}
	
	@Test
	public void shouldMakePost() {
		assertEquals(200, postAddPo.add("cont", authKey, "thread").getStatus());
		Output postResource = postListPo.list("0", "1").posts.get(0);
    assertEquals("cont", postResource.content);
		assertEquals("thread", postResource.threadId);
	}

	@Test 
	public void shouldMakePostWithSameContent() {
		assertEquals(200, postAddPo.add("cont", authKey, "thread").getStatus());
		assertEquals(200, postAddPo.add("cont", authKey, "thread").getStatus());
		List<Output> posts = postListPo.list("0", "2").posts;
    assertEquals("cont", posts.get(0).content);
		assertEquals("thread", posts.get(0).threadId);
    assertEquals("cont", posts.get(1).content);
		assertEquals("thread", posts.get(1).threadId);
	}
	
	@Test 
	public void shouldMakePostWithLongPost() {
		String largeContent = "Lorem ipsum dolor sit amet, consectetur adipisicing elit," +
				"sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
				"Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip" +
				"ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit" +
				"esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
				"sunt in culpa qui officia deserunt mollit anim id est laborum. 	Lorem ipsum dolor sit " +
				"amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore " +
				"magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
				"aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
				"esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident," +
				" sunt in culpa qui officia deserunt mollit anim id est laborum. 	Lorem ipsum dolor sit " +
				"amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore " +
				"magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
				"aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
				"esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
				"sunt in culpa qui officia deserunt mollit anim id est laborum.";
		assertEquals(200, postAddPo.add(largeContent, authKey, "thread").getStatus());
		Output postResource = postListPo.list("0", "1").posts.get(0);
    assertEquals(largeContent, postResource.content);
	}	
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		assertEquals(401, postAddPo.add("cont", authKey+"BAD", "thread").getStatus());
	}
	
	@Test
	public void shouldSeeErrorOnBlankContent() {
		Response response = postAddPo.add(" ", authKey, "thread");
    assertEquals(400, response.getStatus());
    assertEquals("Post must have content", TestUtils.getValidationMessage(response, 0));
	}

	@Test
	public void shouldSeeErrorOnBlankThread() {
		Response response = postAddPo.add("cont", authKey, " ");
    assertEquals(400, response.getStatus());
    assertEquals("Post must include thread id", TestUtils.getValidationMessage(response, 0));
	}
	
	@Test
	public void shouldSeeErrorOnBlanks() {
		Response response = postAddPo.add(" ", authKey, " ");
    assertEquals(400, response.getStatus());
    assertEquals(2, TestUtils.getValidationMessages(response));
	}

	@Test
	public void shouldSeeErrorOnNulls() {
		Response response = postAddPo.add(null, authKey, null);
    assertEquals(400, response.getStatus());
    assertEquals(2, TestUtils.getValidationMessages(response));
	}
}

package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.PostEntity.Output;
import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostEditPO;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;


public class PostEditFunctional {
	
	private LoginResourceReturnData loginResult;
	private Output initialPost;
	private ArrayList<PostEntity.Output> originallyListedPosts;
  private String authKey;
	private UserRegisterPO registerPo;
  private PostAddPO postAddPo;
  private PostsListPO postsListPo;
  private PostEditPO postEditPo;
  private ThreadAddPO threadAddPo = new ThreadAddPO();

	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
	  registerPo = new UserRegisterPO();
	  postAddPo = new PostAddPO();
	  postEditPo = new PostEditPO();
	  postsListPo = new PostsListPO();

	  registerPo.register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	  authKey = loginResult.getAuthKey();

		// Add post
	  assertEquals(200, threadAddPo.add("sub", "cont", "thread", authKey).getStatus());
		assertEquals(200, postAddPo.add("cont", "thread", authKey).getStatus());
		originallyListedPosts = postsListPo.list("0", "10");
		initialPost = originallyListedPosts.get(0); 
	}
	
	@Test
	public void shouldEditPost() {
	  assertEquals(200, postEditPo.edit("sup", initialPost.id, authKey).getStatus());
	  List<Output> posts = postsListPo.list("0", "10");
	  Output post = posts.get(0);
	  assertEquals("sup", post.content);
	  assertEquals(initialPost.creation, post.creation);
	  assertEquals(initialPost.username, post.username);
	  assertEquals(initialPost.threadId, post.threadId);
	  assertEquals(initialPost.adminEdited, post.adminEdited);
	  assertTrue(initialPost.modification < post.modification);
	}

	@Test
	public void shouldSeeErrorOnUnAuthorised() {
	  registerPo.register("other_user", "passy");
		String otherUserAuthKey = new UserLoginPO().login("other_user", "passy").getAuthKey();

		Response response = postEditPo.edit("editeeed", initialPost.id, otherUserAuthKey);
		OutputList postsAfter = postsListPo.list("0", "10");

		assertEquals(403, response.getStatus());
		assertEquals(2, originallyListedPosts.size());
		assertEquals(2, postsAfter.size());
	}

	@Test
	public void shouldAllowAdminToEdit() {
	  registerPo.register("other_user", "passy");
		String otherUserAuthKey = new UserLoginPO().login("other_user", "passy").getAuthKey();

		postAddPo.add("cont", "thread", otherUserAuthKey);
		OutputList posts = postsListPo.list("0", "10");
		
		Response response = postEditPo.edit("editeeed", posts.get(0).id, authKey);
		OutputList postsAfter = postsListPo.list("0", "10");

		assertEquals(200, response.getStatus());
		assertEquals(3, posts.size());
		assertEquals(3, postsAfter.size());
		assertEquals("other_user", posts.get(0).username);
		assertEquals("other_user", postsAfter.get(0).username);
		assertEquals("cont", posts.get(0).content);
		assertEquals("editeeed", postsAfter.get(0).content);
		assertEquals(false, posts.get(0).adminEdited);
		assertEquals(true, postsAfter.get(0).adminEdited);
	}

	@Test
	public void shouldSeeErrorOnBlankContent() {
	  Response response = postEditPo.edit(" ", initialPost.id, authKey);
    assertEquals(400, response.getStatus());
    assertEquals("Post must have content", TestUtils.getValidationMessage(response, 0));
	}
	
}

package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.threads.AddThreadFromPostResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.AddPostPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class AddThreadFromMovedPostFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData adminLoginResult;
	private RegisterPO registerPo;
	private AddPostPO addPostPo;
	
	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		registerPo = new RegisterPO(service);
		TestUtils.deleteTestDb();
	    new RegisterPO(service).register("aaron", "aaron");
		adminLoginResult = new LoginPO(service).login("aaron", "aaron");
		addPostPo = new AddPostPO(service);
	}
	
	@Test
	public void shouldMakeThreadFromPost() {
	    // Arrange -- login as other user
	    registerPo.register("other", "other");
		LoginResourceReturnData loginResult = new LoginPO(service).login("other", "other");
		// Arrange -- add thread and post 
		AddPostResourceReturnData threadRet = addPostPo.add("c", "s", loginResult.getAuthKey());
		addPostPo.add("-", "b", loginResult.getAuthKey(), threadRet.getThread().getId());
		// Arrange -- get posts
		ListPostsResource posts = ListPostsFunctional.listRecentPostsThreads(service);
		assertTrue("Should have two posts, thread starter and first post", posts.getPosts().size()==2);
		
        // Act
		AddThreadFromPostResourceInput threadFromPostInput = new AddThreadFromPostResourceInput();
		threadFromPostInput.setContent("b");
		threadFromPostInput.setSubject("New subject");
		threadFromPostInput.setPostId(posts.getPosts().get(0).getId());
		threadFromPostInput.setUserId("other");
		AddPostResourceReturnData returnData = addThreadFromPost(service, adminLoginResult.getAuthKey(), threadFromPostInput); 
		
		// Assert
		assertTrue(returnData.isSuccessful());
		posts = ListPostsFunctional.listRecentPostsThreads(service);
		assertEquals("New thread has right subject", posts.getPosts().get(0).getSubject(), "New subject");
		assertEquals("New thread has right content", posts.getPosts().get(0).getContent(), "b");
		assertTrue("Still just have two posts, since one's been moved", posts.getPosts().size()==2);
	}

	@Test
	public void shouldThrow401WhenNotAdmin() {
	    // Arrange -- login as other user
	    registerPo.register("other", "other");
		LoginResourceReturnData loginResult = new LoginPO(service).login("other", "other");
		// Arrange -- add thread and post 
		AddPostResourceReturnData threadRet = addPostPo.add("c", "s", loginResult.getAuthKey());
		addPostPo.add("-", "b", loginResult.getAuthKey(), threadRet.getThread().getId());
		// Arrange -- get posts
		ListPostsResource posts = ListPostsFunctional.listRecentPostsThreads(service);
		
        // Act
		AddThreadFromPostResourceInput threadFromPostInput = new AddThreadFromPostResourceInput();
		threadFromPostInput.setContent("b");
		threadFromPostInput.setSubject("New subject");
		threadFromPostInput.setPostId(posts.getPosts().get(0).getId());
		threadFromPostInput.setUserId("other");
		try {
		    addThreadFromPost(service, loginResult.getAuthKey(), threadFromPostInput); 
        } catch (UniformInterfaceException e) {
            assertTrue(e.getResponse().getStatus()==401);
            return;
        }
		assertFalse("Was exception 401 exception", true);
	}
	
	// TODO: 401 when not an admin
	
    public static AddPostResourceReturnData addThreadFromPost(WebResource service, Object authKey, AddThreadFromPostResourceInput input) {
        AddPostResourceReturnData returnData = 
        service
        .path("rest").path("thread").path("frompost")
        .header("AuthKey", authKey)
        .type(MediaType.APPLICATION_JSON)
        .put(AddPostResourceReturnData.class, input);
        return returnData;
    }	
	
}

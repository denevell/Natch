package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.threads.AddThreadFromPostResourceInput;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
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
	
	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron", "aaron");
	    RegisterFunctional.register(service, registerInput);
		LoginResourceInput loginInput = new LoginResourceInput("aaron", "aaron");
		adminLoginResult = LoginFunctional.login(service, loginInput);
	}
	
	@Test
	public void shouldMakeThreadFromPost() {
	    // Arrange -- login as other user
	    RegisterResourceInput registerInput = new RegisterResourceInput("other", "other");
	    RegisterFunctional.register(service, registerInput);
		LoginResourceInput loginInput = new LoginResourceInput("other", "other");
		LoginResourceReturnData loginResult = LoginFunctional.login(service, loginInput);
		// Arrange -- add thread and post 
		AddPostResourceInput threadInput = new AddPostResourceInput("c", "s");
		AddPostResourceReturnData threadRet = AddThreadFunctional.addThread(service, loginResult.getAuthKey(), threadInput);
		AddPostResourceInput postInput = new AddPostResourceInput("-", "b");
		postInput.setThreadId(threadRet.getThread().getId());
		AddPostFunctional.addPost(service, loginResult.getAuthKey(), postInput);
		// Arrange -- get posts
		ListPostsResource posts = ListPostsFunctional.listRecentPostsThreads(service);
		
        // Act
		AddThreadFromPostResourceInput threadFromPostInput = new AddThreadFromPostResourceInput();
		threadFromPostInput.setContent(postInput.getContent());
		threadFromPostInput.setSubject("New subject");
		threadFromPostInput.setPostId(posts.getPosts().get(0).getId());
		threadFromPostInput.setUserId("other");
		AddPostResourceReturnData returnData = addThreadFromPost(service, adminLoginResult.getAuthKey(), threadFromPostInput); 
		
		// Assert
		assertEquals("", returnData.getError());
		assertNotNull(returnData.getThread().getSubject());
		assertTrue(returnData.isSuccessful());
	}
	
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

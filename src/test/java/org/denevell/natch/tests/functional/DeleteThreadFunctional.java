package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.threads.AddThreadResourceInput;
import org.denevell.natch.io.threads.DeleteThreadResourceReturnData;
import org.denevell.natch.io.threads.ThreadsResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class DeleteThreadFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	    // Register
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    RegisterFunctional.register(registerInput, service);
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    loginResult = LoginFunctional.login(loginInput, service);
	}
	
	@Test
	public void shouldDeleteThread() {
		// Arrange 
		AddThreadResourceInput input = new AddThreadResourceInput("a", "a");
		AddThreadResourceInput input1 = new AddThreadResourceInput("b", "b");;
		AddThreadFunctional.addThread(input, service, loginResult.getAuthKey());
		AddThreadFunctional.addThread(input1, service, loginResult.getAuthKey());
		ThreadsResource listThreads = ListThreadsFunctional.listTenThreads(service); 
		
		// Act
		DeleteThreadResourceReturnData ret = service.path("rest").path("threads").path("del")
		.path(String.valueOf(listThreads.getThreads().get(0).getId()))
		.header("AuthKey", loginResult.getAuthKey())
		.entity(null)
		.delete(DeleteThreadResourceReturnData.class);
		ThreadsResource listThreadsAfter = ListThreadsFunctional.listTenThreads(service);
		
		// Assert
		assertEquals("", ret.getError());
		assertTrue(ret.isSuccessful());		
		assertEquals(2, listThreads.getThreads().size());		
		assertEquals(1, listThreadsAfter.getThreads().size());		
		assertEquals("a", listThreadsAfter.getThreads().get(0).getSubject());
	}
	
//	@Test
//	public void shouldSeeErrorOnUnAuthorised() {
//		// Arrange 
//		// Register other user
//	    LoginResourceInput loginInput1 = new LoginResourceInput("aaron1@aaron.com", "passy");
//		service
//	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
//	    	.put(RegisterResourceReturnData.class, loginInput1);
//		LoginResourceReturnData loginResult1 = service
//	    		.path("rest").path("user").path("login")
//	    		.type(MediaType.APPLICATION_JSON)
//	    		.post(LoginResourceReturnData.class, loginInput1);				
//		// Make post with first user
//		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
//		service
//		.path("rest").path("post").path("add")
//	    .type(MediaType.APPLICATION_JSON)
//		.header("AuthKey", loginResult.getAuthKey())
//    	.put(AddPostResourceReturnData.class, input); 
//		ListPostsResource listPosts = service
//		.path("rest").path("post").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 		
//		
//		// Act - delete with second user then list
//		DeletePostResourceReturnData ret = service.path("rest").path("post").path("del")
//		.path(String.valueOf(listPosts.getPosts().get(0).getId()))
//		.header("AuthKey", loginResult1.getAuthKey())
//		.entity(null)
//		.delete(DeletePostResourceReturnData.class);
//		ListPostsResource listPostsAfter = service
//		.path("rest").path("post").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 		
//		
//		// Assert
//		assertEquals(rb.getString(Strings.post_not_yours), ret.getError());
//		assertFalse(ret.isSuccessful());		
//		assertEquals(1, listPosts.getPosts().size());		
//		assertEquals(1, listPostsAfter.getPosts().size());		
//	}
//	
//	@Test
//	public void shouldSeeErrorOnUnknownThread() {
//		// Arrange 
//		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
//		service
//		.path("rest").path("post").path("add")
//	    .type(MediaType.APPLICATION_JSON)
//		.header("AuthKey", loginResult.getAuthKey())
//    	.put(AddPostResourceReturnData.class, input); 
//		ListPostsResource listPosts = service
//		.path("rest").path("post").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 		
//		
//		// Act
//		DeletePostResourceReturnData ret = service.path("rest").path("post").path("del")
//		.path(String.valueOf(listPosts.getPosts().get(0).getId()+1))
//		.header("AuthKey", loginResult.getAuthKey())
//		.entity(null)
//		.delete(DeletePostResourceReturnData.class);
//		ListPostsResource listPostsAfter = service
//		.path("rest").path("post").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 		
//		
//		// Assert
//		assertEquals(rb.getString(Strings.post_doesnt_exist), ret.getError());
//		assertFalse(ret.isSuccessful());		
//		assertEquals(1, listPosts.getPosts().size());		
//		assertEquals(1, listPostsAfter.getPosts().size());				
//	}
	
}

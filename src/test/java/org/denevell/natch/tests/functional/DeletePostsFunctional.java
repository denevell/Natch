package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.DeletePostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.ui.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class DeletePostsFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;
	private RegisterPO registerPo;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	    registerPo = new RegisterPO(service);
	    registerPo.register("aaron@aaron.com", "passy");
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);				
	}
	
	@Test
	public void shouldDeletePost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("a", "b", "thread");	
		AddPostResourceInput input2 = new AddPostResourceInput("d", "e", "thread");	

		AddPostResourceReturnData res = service
		.path("rest").path("post").path("addthread")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input);
		assertTrue("Added thraed", res.isSuccessful());				

		res = service
		.path("rest").path("post").path("addthread")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input2);
		assertTrue("Added thraed", res.isSuccessful());				

		ListPostsResource listPostsBefore = 
		service.path("rest").path("post").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 		
		
		// Act
		DeletePostResourceReturnData ret = service.path("rest").path("post").path("del")
		.path(String.valueOf(listPostsBefore.getPosts().get(0).getId()))
		.header("AuthKey", loginResult.getAuthKey())
		.entity(null)
		.delete(DeletePostResourceReturnData.class);
		ListPostsResource listPostsAfter = service
		.path("rest").path("post").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 		
		
		// Assert
		assertEquals("", ret.getError());
		assertTrue(ret.isSuccessful());		
		assertEquals(2, listPostsBefore.getPosts().size());		
		assertEquals(1, listPostsAfter.getPosts().size());		
	}
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		// Arrange 
		// Register other user
	    registerPo.register("aaron1@aaron.com", "passy");
	    LoginResourceInput loginInput1 = new LoginResourceInput("aaron1@aaron.com", "passy");
		LoginResourceReturnData loginResult1 = LoginFunctional.login(service, loginInput1);

		// Make post with user one 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		ListPostsResource listPosts = service
		.path("rest").path("post").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 		
		
		// Act - delete with second user
		DeletePostResourceReturnData ret = service.path("rest").path("post").path("del")
		.path(String.valueOf(listPosts.getPosts().get(0).getId()))
		.header("AuthKey", loginResult1.getAuthKey())
		.entity(null)
		.delete(DeletePostResourceReturnData.class);
		ListPostsResource listPostsAfter = service
		.path("rest").path("post").path("0").path("10")
		.header("AuthKey", loginResult1.getAuthKey())
    	.get(ListPostsResource.class); 		
		
		// Assert
		assertEquals(rb.getString(Strings.post_not_yours), ret.getError());
		assertFalse(ret.isSuccessful());		
		assertEquals(1, listPosts.getPosts().size());		
		assertEquals(1, listPostsAfter.getPosts().size());		
	}
	
    @Test
    public void shouldAllowAdminToDelete() {
        // Arrange 
        // Register other user
	    registerPo.register("aaron1@aaron.com", "passy");
        LoginResourceInput loginInput1 = new LoginResourceInput("aaron1@aaron.com", "passy");
        LoginResourceReturnData loginResult1 = LoginFunctional.login(service, loginInput1);

        // Make post with user two
        AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
        service
        .path("rest").path("post").path("add")
        .type(MediaType.APPLICATION_JSON)
        .header("AuthKey", loginResult1.getAuthKey())
        .put(AddPostResourceReturnData.class, input); 
        ListPostsResource listPosts = service
        .path("rest").path("post").path("0").path("10")
        .header("AuthKey", loginResult1.getAuthKey())
        .get(ListPostsResource.class);      
        
        // Act - delete with first user, admin user
        DeletePostResourceReturnData ret = service.path("rest").path("post").path("del")
        .path(String.valueOf(listPosts.getPosts().get(0).getId()))
        .header("AuthKey", loginResult.getAuthKey())
        .entity(null)
        .delete(DeletePostResourceReturnData.class);
        ListPostsResource listPostsAfter = service
        .path("rest").path("post").path("0").path("10")
        .header("AuthKey", loginResult.getAuthKey())
        .get(ListPostsResource.class);      
        
        // Assert
        assertEquals("", ret.getError());
        assertTrue(ret.isSuccessful());     
        assertEquals(1, listPosts.getPosts().size());       
        assertEquals(0, listPostsAfter.getPosts().size());      
    }	
	
	@Test
	public void shouldSeeErrorOnUnknownPost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		ListPostsResource listPosts = service
		.path("rest").path("post").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 		
		
		// Act
		DeletePostResourceReturnData ret = service.path("rest").path("post").path("del")
		.path(String.valueOf(listPosts.getPosts().get(0).getId()+1))
		.header("AuthKey", loginResult.getAuthKey())
		.entity(null)
		.delete(DeletePostResourceReturnData.class);
		ListPostsResource listPostsAfter = service
		.path("rest").path("post").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 		
		
		// Assert
		assertEquals(rb.getString(Strings.post_doesnt_exist), ret.getError());
		assertFalse(ret.isSuccessful());		
		assertEquals(1, listPosts.getPosts().size());		
		assertEquals(1, listPostsAfter.getPosts().size());				
	}
	
	public static DeletePostResourceReturnData deletePost(WebResource service, long postId, String authKey) {
		DeletePostResourceReturnData ret = service.path("rest").path("post").path("del")
		.path(String.valueOf(postId))
		.header("AuthKey", authKey)
		.entity(null)
		.delete(DeletePostResourceReturnData.class);	
		return ret;
	}
	
}

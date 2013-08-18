package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.threads.AddThreadResourceInput;
import org.denevell.natch.io.threads.EditThreadResourceReturnData;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.io.threads.ThreadsResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class EditThreadFunctional {
	
	private WebResource service;
	private LoginResourceReturnData loginResult;
	private AddThreadResourceInput initalInput;
	private ThreadResource initialThread;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private ThreadsResource originallyListedPosts;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		// Delete all users
		TestUtils.deleteTestDb();
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy"); // Register service
	    service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		login(loginInput);			
		// Add post
		initalInput = new AddThreadResourceInput("sub", "cont");
		AddThreadFunctional.addThread(initalInput,  service, loginResult.getAuthKey());
		// List it
		originallyListedPosts = ListThreadsFunctional.listTenThreads(service);
		// save it
		initialThread = originallyListedPosts.getThreads().get(0);
		// Pre assert
		assertEquals(initalInput.getContent(), initialThread.getContent());
		assertEquals(initalInput.getSubject(), initialThread.getSubject());
	}

	public void login(LoginResourceInput loginInput) {
		loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
	}
	
	@Test
	public void shouldEditPost() {
		// Arrange
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("sup");
		editedInput.setSubject("sup two?");
		editedInput.setTags(Arrays.asList(new String[] {"tagx", "tagy"}));
		
		// Act - edit then list
		EditThreadResourceReturnData editReturnData = service
		.path("rest").path("threads").path("edit")
		.path(String.valueOf(initialThread.getId()))
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.post(EditThreadResourceReturnData.class, editedInput); 		
		ThreadsResource newListedPosts = ListThreadsFunctional.listTenThreads(service);		

		// Assert
		assertEquals("", editReturnData.getError());
		assertTrue(editReturnData.isSuccessful());		
		assertEquals(initialThread.getId(), newListedPosts.getThreads().get(0).getId());
		assertEquals(initialThread.getCreation(), newListedPosts.getThreads().get(0).getCreation());
		assertEquals(initialThread.getAuthor(), newListedPosts.getThreads().get(0).getAuthor());
		assertEquals("tagx", newListedPosts.getThreads().get(0).getTags().get(1));
		assertEquals("tagy", newListedPosts.getThreads().get(0).getTags().get(0));
		assertEquals("sup", newListedPosts.getThreads().get(0).getContent());
		assertEquals("sup two?", newListedPosts.getThreads().get(0).getSubject());
		assertTrue(newListedPosts.getThreads().get(0).getModification() > initialThread.getModification());
	}
	
//	@Test
//	public void shouldSeeErrorOnUnAuthorised() {
//		// Arrange
//		EditPostResource editedInput = new EditPostResource();
//		editedInput.setContent("sup");
//		editedInput.setSubject("sup two?");
//		// Login with another user
//	    LoginResourceInput loginInput1 = new LoginResourceInput("aaron1@aaron.com", "passy");
//		service
//	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
//	    	.put(RegisterResourceReturnData.class, loginInput1);
//		LoginResourceReturnData loginResult1 = service
//	    		.path("rest").path("user").path("login")
//	    		.type(MediaType.APPLICATION_JSON)
//	    		.post(LoginResourceReturnData.class, loginInput1);				
//		
//		// Act - edit with different user then list
//		EditPostResourceReturnData editReturnData = service
//		.path("rest").path("post").path("edit")
//		.path(String.valueOf(initialThread.getId()))
//	    .type(MediaType.APPLICATION_JSON)
//		.header("AuthKey", loginResult1.getAuthKey())
//    	.post(EditPostResourceReturnData.class, editedInput); 		
//		ListPostsResource newListedPosts = service
//		.path("rest").path("post").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 			
//		
//		// Assert
//		assertEquals(rb.getString(Strings.post_not_yours), editReturnData.getError());
//		assertFalse(editReturnData.isSuccessful());		
//		assertEquals(initalInput.getContent(), newListedPosts.getPosts().get(0).getContent());
//		assertEquals(initalInput.getSubject(), newListedPosts.getPosts().get(0).getSubject());
//	}
//	
//	@Test
//	public void shouldSeeErrorOnBlankSubject() {
//		// Arrange
//		EditPostResource editedInput = new EditPostResource();
//		editedInput.setContent("sdfsfd");
//		editedInput.setSubject(" ");
//		
//		// Act - edit then list
//		EditPostResourceReturnData editReturnData = service
//		.path("rest").path("post").path("edit")
//		.path(String.valueOf(initialThread.getId()))
//	    .type(MediaType.APPLICATION_JSON)
//		.header("AuthKey", loginResult.getAuthKey())
//    	.post(EditPostResourceReturnData.class, editedInput); 		
//		ListPostsResource newListedPosts = service
//		.path("rest").path("post").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 			
//		
//		// Assert
//		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), editReturnData.getError());
//		assertFalse(editReturnData.isSuccessful());		
//		assertEquals(initalInput.getContent(), newListedPosts.getPosts().get(0).getContent());
//		assertEquals(initalInput.getSubject(), newListedPosts.getPosts().get(0).getSubject());
//	}
//	
//	@Test
//	public void shouldSeeErrorOnBlankContent() {
//		// Arrange
//		EditPostResource editedInput = new EditPostResource();
//		editedInput.setContent(" ");
//		editedInput.setSubject("dsfsdf");
//		
//		// Act - edit then list
//		EditPostResourceReturnData editReturnData = service
//		.path("rest").path("post").path("edit")
//		.path(String.valueOf(initialThread.getId()))
//	    .type(MediaType.APPLICATION_JSON)
//		.header("AuthKey", loginResult.getAuthKey())
//    	.post(EditPostResourceReturnData.class, editedInput); 		
//		ListPostsResource newListedPosts = service
//		.path("rest").path("post").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 			
//		
//		// Assert
//		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), editReturnData.getError());
//		assertFalse(editReturnData.isSuccessful());		
//		assertEquals(initalInput.getContent(), newListedPosts.getPosts().get(0).getContent());
//		assertEquals(initalInput.getSubject(), newListedPosts.getPosts().get(0).getSubject());
//	}
//	
//	@Test
//	public void shouldSeeErrorOnBlanks() {
//		// Arrange
//		EditPostResource editedInput = new EditPostResource();
//		editedInput.setContent(" ");
//		editedInput.setSubject(" ");
//		
//		// Act - edit then list
//		EditPostResourceReturnData editReturnData = service
//		.path("rest").path("post").path("edit")
//		.path(String.valueOf(initialThread.getId()))
//	    .type(MediaType.APPLICATION_JSON)
//		.header("AuthKey", loginResult.getAuthKey())
//    	.post(EditPostResourceReturnData.class, editedInput); 		
//		ListPostsResource newListedPosts = service
//		.path("rest").path("post").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 			
//		
//		// Assert
//		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), editReturnData.getError());
//		assertFalse(editReturnData.isSuccessful());		
//		assertEquals(initalInput.getContent(), newListedPosts.getPosts().get(0).getContent());
//		assertEquals(initalInput.getSubject(), newListedPosts.getPosts().get(0).getSubject());
//	}
}

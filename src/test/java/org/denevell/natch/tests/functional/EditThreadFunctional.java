package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.threads.AddThreadResourceInput;
import org.denevell.natch.io.threads.EditThreadResourceInput;
import org.denevell.natch.io.threads.EditThreadResourceReturnData;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.io.threads.ThreadsResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class EditThreadFunctional {
	
	private static WebResource service;
	private LoginResourceReturnData loginResult;
	private AddThreadResourceInput initalInput;
	private ThreadResource initialThread;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private ThreadsResource originallyListedPosts;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
		// Delete all users
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy"); // Register service
	    RegisterFunctional.register(registerInput, service);
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		loginResult = LoginFunctional.login(loginInput, service);			
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
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		// Arrange
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("sup");
		editedInput.setSubject("sup two?");
		editedInput.setTags(Arrays.asList(new String[] {"tagx", "tagy"}));
		// Login with other user
	    RegisterResourceInput regInput1 = new RegisterResourceInput("aaron1@aaron.com", "passy");
	    RegisterFunctional.register(regInput1, service);
	    LoginResourceInput loginInput1 = new LoginResourceInput("aaron1@aaron.com", "passy");
		LoginResourceReturnData loginResult1 = LoginFunctional.login(loginInput1, service);			
		
		// Act - edit with other user then list
		EditThreadResourceReturnData editReturnData = service
		.path("rest").path("threads").path("edit")
		.path(String.valueOf(initialThread.getId()))
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult1.getAuthKey())
    	.post(EditThreadResourceReturnData.class, editedInput); 		
		ThreadsResource newListedPosts = ListThreadsFunctional.listTenThreads(service);		

		// Assert
		assertEquals(rb.getString(Strings.post_not_yours), editReturnData.getError());
		assertFalse(editReturnData.isSuccessful());		
		assertEquals(initalInput.getContent(), newListedPosts.getThreads().get(0).getContent());
		assertEquals(initalInput.getSubject(), newListedPosts.getThreads().get(0).getSubject());
		
	}
	
	@Test
	public void shouldSeeErrorOnBlankSubject() {
		// Arrange
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("sup");
		editedInput.setSubject(" ");
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
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), editReturnData.getError());
		assertFalse(editReturnData.isSuccessful());		
		assertEquals(initalInput.getContent(), newListedPosts.getThreads().get(0).getContent());
		assertEquals(initalInput.getSubject(), newListedPosts.getThreads().get(0).getSubject());
	}
	
	@Test
	public void shouldSeeErrorOnBlankContent() {
		// Arrange
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent(" ");
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
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), editReturnData.getError());
		assertFalse(editReturnData.isSuccessful());		
		assertEquals(initalInput.getContent(), newListedPosts.getThreads().get(0).getContent());
		assertEquals(initalInput.getSubject(), newListedPosts.getThreads().get(0).getSubject());
	}
	
	@Test
	public void shouldSeeErrorOnBlanks() {
		// Arrange
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent(" ");
		editedInput.setSubject(" ");
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
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), editReturnData.getError());
		assertFalse(editReturnData.isSuccessful());		
		assertEquals(initalInput.getContent(), newListedPosts.getThreads().get(0).getContent());
		assertEquals(initalInput.getSubject(), newListedPosts.getThreads().get(0).getSubject());
	}
	
	public static EditThreadResourceReturnData edit(
			WebResource service, String id, String authKey, EditThreadResourceInput editedInput) {
		EditThreadResourceReturnData r = service
		.path("rest").path("threads").path("edit")
		.path(String.valueOf(id))
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", authKey)
    	.post(EditThreadResourceReturnData.class, editedInput); 		
		assertTrue("Edit was a success", r.isSuccessful());
		return r;
	}
}

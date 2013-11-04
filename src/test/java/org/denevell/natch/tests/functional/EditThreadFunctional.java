package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class EditThreadFunctional {
    
	private WebResource service;
    private String authKey;
    ResourceBundle rb = Strings.getMainResourceBundle();

    @Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		// Delete all users and add one new
		TestUtils.deleteTestDb();
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    RegisterFunctional.register(service, registerInput);
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    LoginResourceReturnData loginResult = LoginFunctional.login(service, loginInput);
	    authKey = loginResult.getAuthKey();
	}
	
	@Test
	public void shouldSeeErrorOnBlankSubject() {
        // Arrange
		AddPostResourceInput threadInput = new AddPostResourceInput("thread", "threadc");
	    AddThreadFunctional.addThread(service, authKey, threadInput);
	    PostResource thread = ListPostsFunctional.listRecentPostsThreads(service).getPosts().get(0);
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("sdfsfd");
		editedInput.setSubject(" ");
		
		// Act - edit then list
		EditPostResourceReturnData editReturnData = editThread(service, authKey, thread.getId(), editedInput); 		
		ListPostsResource newListedPosts = ListPostsFunctional.listRecentPostsThreads(service);
		
		// Assert
		assertFalse(editReturnData.isSuccessful());		
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), editReturnData.getError());
		assertEquals(thread.getContent(), newListedPosts.getPosts().get(0).getContent());
		assertEquals(thread.getSubject(), newListedPosts.getPosts().get(0).getSubject());
	}

    public static EditPostResourceReturnData editThread(WebResource service, String authKey, long postId, EditPostResource editedInput) {
        return service
		.path("rest").path("post").path("editthread")
		.path(String.valueOf(postId))
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", authKey)
    	.post(EditPostResourceReturnData.class, editedInput);
    }

}

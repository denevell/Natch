package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.io.threads.EditThreadResource;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class EditThreadFunctional {
    
	private WebTarget service;
    private String authKey;
    ResourceBundle rb = Strings.getMainResourceBundle();

    @Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		// Delete all users and add one new
		TestUtils.deleteTestDb();
	    new RegisterPO().register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO().login("aaron@aaron.com", "passy");
	    authKey = loginResult.getAuthKey();
	}
	
	@Test
	public void shouldSeeErrorOnBlankSubject() {
		PostResource thread = null;
		try {
			AddPostResourceInput threadInput = new AddPostResourceInput("thread", "threadc");
			AddThreadFunctional.addThread(service, authKey, threadInput);
			thread = ListPostsFunctional.listRecentPostsThreads(service).getPosts().get(0);
			EditThreadResource editedInput = new EditThreadResource();
			editedInput.setContent("sdfsfd");
			editedInput.setSubject(" ");
			editThread(service, authKey, thread.getId(), editedInput); 		
			assertFalse("Expected 400 error", true);
		} catch(WebApplicationException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}	
		
		ListPostsResource newListedPosts = ListPostsFunctional.listRecentPostsThreads(service);
		assertEquals(thread.getContent(), newListedPosts.getPosts().get(0).getContent());
		assertEquals(thread.getSubject(), newListedPosts.getPosts().get(0).getSubject());
	}

	@Test
	public void shouldSeeErrorOnBlanks() {
		PostResource thread = null;
		try {
			AddPostResourceInput threadInput = new AddPostResourceInput("thread", "threadc");
			AddThreadFunctional.addThread(service, authKey, threadInput);
			thread = ListPostsFunctional.listRecentPostsThreads(service).getPosts().get(0);
			EditThreadResource editedInput = new EditThreadResource();
			editedInput.setContent(" ");
			editedInput.setSubject(" ");
			editThread(service, authKey, thread.getId(), editedInput); 		
			assertFalse("Expected 400 error", true);
		} catch(WebApplicationException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}	
		
		ListPostsResource newListedPosts = ListPostsFunctional.listRecentPostsThreads(service);
		assertEquals(thread.getContent(), newListedPosts.getPosts().get(0).getContent());
		assertEquals(thread.getSubject(), newListedPosts.getPosts().get(0).getSubject());
	}

    @Test
    public void shouldSeeErrorOnLargeSubject() {
		AddPostResourceInput threadInput = new AddPostResourceInput("thread", "threadc");
	    AddThreadFunctional.addThread(service, authKey, threadInput);
	    PostResource thread = ListPostsFunctional.listRecentPostsThreads(service).getPosts().get(0);
		EditThreadResource editedInput = new EditThreadResource();
		editedInput.setContent("sdfsfd");
		editedInput.setSubject("sdfsdfassdfklasjdflksdfkjasfl;kjasdl;kfjsd;lfjasdl;fjsal;fjas;ldfjasld;fjasl;fjasl;fjsal;dfjsdlfkjasdjf;lkasjf;lajsdfl;ajsdf;ljasdf;lkjsdlfjasd;lkfjasl;dfkjasdlfjasdlfkjasdlfjsadlkfjasldfkjsadlfkjlkdfj;alskdfjasl;kdfjasl;dfj;alsdfjal;skdfj;alsdkjf;slajdflk;asjflkasdjflasjflkajdflkasdjflksdjflkasdjflkasjdflkasdjf;lasdjf;lasjdf;lkasdjfl;sjadfl;asjdfl;asjdf;lasjdf");
		
		// Act - edit then list
		EditPostResourceReturnData editReturnData = editThread(service, authKey, thread.getId(), editedInput); 		
		ListPostsResource newListedPosts = ListPostsFunctional.listRecentPostsThreads(service);
		
		// Assert
		assertFalse(editReturnData.isSuccessful());		
		assertEquals(rb.getString(Strings.subject_too_large), editReturnData.getError());
		assertEquals(thread.getContent(), newListedPosts.getPosts().get(0).getContent());
		assertEquals(thread.getSubject(), newListedPosts.getPosts().get(0).getSubject());
    }

	@SuppressWarnings("serial")
    @Test
	public void shouldSeeErrorOnLargeTag() {
        // Arrange
		AddPostResourceInput threadInput = new AddPostResourceInput("thread", "threadc");
	    AddThreadFunctional.addThread(service, authKey, threadInput);
	    PostResource thread = ListPostsFunctional.listRecentPostsThreads(service).getPosts().get(0);
		EditThreadResource editedInput = new EditThreadResource();
		editedInput.setContent("sdfsfd");
		editedInput.setSubject("sdfsdf");
		editedInput.setTags(new ArrayList<String>(){{
		   add("small");
		   add("thisislargerthan20charactersdefiniteily");
		}});
		
		// Act - edit then list
		EditPostResourceReturnData editReturnData = editThread(service, authKey, thread.getId(), editedInput); 		
		ListPostsResource newListedPosts = ListPostsFunctional.listRecentPostsThreads(service);
		
		// Assert
		assertFalse(editReturnData.isSuccessful());		
		assertEquals(rb.getString(Strings.tag_too_large), editReturnData.getError());
		assertEquals(thread.getContent(), newListedPosts.getPosts().get(0).getContent());
		assertEquals(thread.getSubject(), newListedPosts.getPosts().get(0).getSubject());
	}

    public static EditPostResourceReturnData editThread(WebTarget service, String authKey, long postId, EditThreadResource editedInput) {
        return service
		.path("rest").path("post").path("editthread")
		.path(String.valueOf(postId)).request()
		.header("AuthKey", authKey)
    	.post(Entity.entity(editedInput, MediaType.APPLICATION_JSON),EditPostResourceReturnData.class);
    }

}

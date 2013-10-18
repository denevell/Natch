package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.threads.ListThreadsResource;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class ListThreadsFunctional {
	
	private LoginResourceReturnData loginResult;
	private WebResource service;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		// Delete all users
		TestUtils.deleteTestDb();
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    // Register
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);		
	}

	
	@Test
	public void shouldListThreadsByPostLastEntered() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", "t");
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		addThread(input); 
		addThread(input1); 
		addThread(input2); 
		
		ListThreadsResource returnData = listThreads(); 
		
		// Assert
		assertEquals(2, returnData.getNumOfThreads());
		assertEquals(2, returnData.getThreads().size());
		assertTrue(returnData.getThreads().get(0).getId()!=null);
		assertTrue(returnData.getThreads().get(1).getId()!=null);
		assertEquals("sub", returnData.getThreads().get(0).getSubject());
		assertEquals("sub1", returnData.getThreads().get(1).getSubject());
		assertEquals("other", returnData.getThreads().get(1).getId());		
		assertEquals("t", returnData.getThreads().get(0).getId());		
	}
	
	@Test
	public void shouldListThreadsWithModificationDateAsLatestPost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", "t");
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "t");
		addThread(input); 
		addPost(input1); 
		
		// Act
		ListThreadsResource threads = listThreads(); 
		ThreadResource thread = listThreadById("t"); 		
		
		// Assert
		assertEquals("Has latest post's modificaton date", 
				threads.getThreads().get(0).getModification(), 
				thread.getPosts().get(1).getModification());

		assertNotEquals("Doesn't have latest post's modificaton date", 
				threads.getThreads().get(0).getModification(), 
				thread.getPosts().get(0).getModification());
	}
	
	@Test
	public void shouldListThreadsByPostLastEnteredWithLimit() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", "t");
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		addThread(input); 
		addThread(input1); 
		addThread(input2); 
		
		// Act
		ListThreadsResource returnData = service
		.path("rest").path("threads").path("1").path("1")
    	.get(ListThreadsResource.class); 
		
		// Assert)
		assertEquals(2, returnData.getNumOfThreads());
		assertEquals(1, returnData.getThreads().size());
		assertEquals("sub1", returnData.getThreads().get(0).getSubject());
		assertEquals("other", returnData.getThreads().get(0).getId());
	}	
	
	@Test
	public void shouldListThreadsByTag() {
		// Arrange 
		AddPostResourceInput input0 = new AddPostResourceInput("x", "x", "x");
		input0.setTags(Arrays.asList(new String[] {"onetag", "f"})); 
		// We need two in the list, since our deserialisation doesn't like single item lists.
		// Works fine on the command line though: curl url ... -d '{"content":"xxx", "posttags": ["t"], "subject":"xxx", "thread":"NEW"}'
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", "t");
		input.setTags(Arrays.asList(new String[] {"tag", "onetag"}));
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		input2.setTags(Arrays.asList(new String[] {"tag", "onetag"}));
		addThread(input0); 
		addThread(input); 
		addThread(input1); 
		addThread(input2); 
		
		// Act
		ListThreadsResource returnData = service
		.path("rest").path("threads").path("onetag").path("0").path("10")
    	.get(ListThreadsResource.class); 
		
		// Assert
		assertEquals(2, returnData.getNumOfThreads());
		assertEquals(2, returnData.getThreads().size());
		assertEquals("sub", returnData.getThreads().get(0).getSubject());
		assertEquals("t", returnData.getThreads().get(0).getId());
		assertEquals("onetag", returnData.getThreads().get(0).getTags().get(1));
		
		assertEquals("x", returnData.getThreads().get(1).getSubject());
		assertEquals("x", returnData.getThreads().get(1).getId());
		assertEquals("onetag", returnData.getThreads().get(1).getTags().get(0));
	}	
	
	@Test
	public void shouldListThreadsByTagWithLimit() {
		// Arrange 
		AddPostResourceInput input0 = new AddPostResourceInput("x", "x", "x");
		input0.setTags(Arrays.asList(new String[] {"onetag", "f"})); 
		// We need two in the list, since our deserialisation doesn't like single item lists.
		// Works fine on the command line though: curl url ... -d '{"content":"xxx", "posttags": ["t"], "subject":"xxx", "thread":"NEW"}'
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", "t");
		input.setTags(Arrays.asList(new String[] {"tag", "onetag"}));
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		input2.setTags(Arrays.asList(new String[] {"tag", "onetag"}));
		addThread(input0); 
		addThread(input); 
		addThread(input1); 
		addThread(input2); 
		
		// Act
		ListThreadsResource returnData = service
		.path("rest").path("threads").path("onetag").path("1").path("1")
    	.get(ListThreadsResource.class); 
		
		// Assert
		assertEquals(2, returnData.getNumOfThreads());
		assertEquals(1, returnData.getThreads().size());
		assertEquals("x", returnData.getThreads().get(0).getSubject());
		assertEquals("x", returnData.getThreads().get(0).getId());
		assertEquals("onetag", returnData.getThreads().get(0).getTags().get(0));
	}

	@Test
	public void shouldListThreadsWithThreadWithLastModifiedContentFirst() {	
		// Arrange
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		addThread(input1); 
		addThread(input2); 		
		// Note: input2 was added last so should be first in the list
		// Arrange - list them
		ListPostsResource listedPosts = service
		.path("rest").path("post").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "sub2", listedPosts.getPosts().get(0).getSubject());
		
		// Act - modify input 1
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("blar");
		editedInput.setSubject("blar2");
		EditPostResourceReturnData editReturnData = service
		.path("rest").path("post").path("editthread")
		.path(String.valueOf(listedPosts.getPosts().get(1).getId()))
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.post(EditPostResourceReturnData.class, editedInput); 			
		assertTrue(editReturnData.isSuccessful());
		
		// Assert - we've now got input1 first
		ListThreadsResource listedThreads = service
		.path("rest").path("threads").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListThreadsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "blar2", listedThreads.getThreads().get(0).getSubject());
	}
	
	@Ignore // Until we somehow get the edit ids when listing a post
	@SuppressWarnings("serial")
	@Test
	public void shouldListThreadsByTagWithThreadWithLastModifiedContentFirst() {	
		// Arrange
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		input1.setTags(new ArrayList<String>(){{add("a");add("b");}});
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		input2.setTags(new ArrayList<String>(){{add("a");add("b");}});
		addThread(input1); 
		addThread(input2); 	
		// Note: input2 was added last so should be first in the list
		// Arrange - list them
		ListThreadsResource listedPosts = service
		.path("rest").path("threads").path("a").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListThreadsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "sub2", listedPosts.getThreads().get(0).getSubject());
		
		// Act - modify input 1
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("blar");
		editedInput.setSubject("blar2");
		editedInput.setTags(new ArrayList<String>(){{add("a");add("b");}});
		EditPostResourceReturnData editReturnData = service
		.path("rest").path("post").path("editthread")
		.path(String.valueOf(listedPosts.getThreads().get(1).getId()))
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.post(EditPostResourceReturnData.class, editedInput); 			
		assertTrue(editReturnData.isSuccessful());
		
		// Assert - we've now got input1 first
		listedPosts = service
		.path("rest").path("threads").path("a").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListThreadsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "blar2", listedPosts.getThreads().get(0).getSubject());
	}

	public void addPost(AddPostResourceInput input2) {
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input2);
	}		

	public void addThread(AddPostResourceInput input2) {
		AddPostResourceReturnData res = service
		.path("rest").path("post").path("addthread")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input2);
		assertTrue("Added thraed", res.isSuccessful());
	}		
	
	public ThreadResource listThreadById(String id) {
		ThreadResource thread = service
		.path("rest").path("post").path("thread").path(id).path("0").path("10")
		.get(ThreadResource.class);
		return thread;
	}		
	
	public ListThreadsResource listThreads() {
		ListThreadsResource threads = service
		.path("rest").path("threads").path("0").path("10")
    	.get(ListThreadsResource.class);
		return threads;
	}	
}

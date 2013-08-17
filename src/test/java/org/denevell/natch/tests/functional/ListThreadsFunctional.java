package org.denevell.natch.tests.functional;

import static org.junit.Assert.*;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.threads.AddThreadResourceInput;
import org.denevell.natch.io.threads.ThreadsResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
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
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron", "passy");
	    // Register
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron", "passy");
		loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);		
	}
	
	@SuppressWarnings("serial")
	@Test
	public void shouldListThreadsByPostLastEnteredWithLimit() {
		// Arrange 
		AddThreadResourceInput input = new AddThreadResourceInput("sub", "cont");
		input.setTags(new ArrayList<String>() {{add("a"); add("b");}});
		AddThreadResourceInput input1 = new AddThreadResourceInput("sub1", "cont1");
		AddThreadResourceInput input2 = new AddThreadResourceInput("sub2", "cont2");
		AddThreadResourceInput input3 = new AddThreadResourceInput("sub3", "cont3");
		AddThreadFunctional.addThread(input, service, loginResult.getAuthKey()); // page 2
		AddThreadFunctional.addThread(input1, service, loginResult.getAuthKey());  // page 2
		AddThreadFunctional.addThread(input2, service, loginResult.getAuthKey()); 
		AddThreadFunctional.addThread(input3, service, loginResult.getAuthKey()); 
		
		// Act
		ThreadsResource returnData = service
		.path("rest").path("threads").path("2").path("2")
    	.get(ThreadsResource.class); 
		
		// Assert
		assertEquals(2, returnData.getThreads().size());
		assertEquals(2, returnData.getPage());
		assertEquals(2, returnData.getTotalPages());
		assertEquals("sub1", returnData.getThreads().get(0).getSubject());
		assertEquals("cont1", returnData.getThreads().get(0).getContent());
		assertTrue("Id is okay", returnData.getThreads().get(0).getId()>0);
		assertEquals("sub", returnData.getThreads().get(1).getSubject());
		assertEquals("cont", returnData.getThreads().get(1).getContent());
		assertEquals("a", returnData.getThreads().get(1).getTags().get(0));
		assertEquals("b", returnData.getThreads().get(1).getTags().get(1));
		assertEquals("aaron", returnData.getThreads().get(1).getAuthor());
	}	
	
	/*@Test
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
		addPost(input0); 
		addPost(input); 
		addPost(input1); 
		addPost(input2); 
		
		// Act
		ThreadsResource returnData = service
		.path("rest").path("threads").path("onetag").path("1").path("1")
    	.get(ThreadsResource.class); 
		
		// Assert
		assertEquals(1, returnData.getThreads().size());
		assertEquals("x", returnData.getThreads().get(0).getSubject());
		assertEquals("x", returnData.getThreads().get(0).getContent());
		assertEquals("x", returnData.getThreads().get(0).getId());
		assertEquals("onetag", returnData.getThreads().get(0).getTags().get(0));
	}
*/
//	@Test
//	public void shouldListThreadsWithLastModifiedContentFirst() {	
//		// Arrange
//		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
//		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
//		addPost(input1); 
//		addPost(input2); 		
//		// Note: input2 was added last so should be first in the list
//		// Arrange - list them
//		ListPostsResource listedPosts = service
//		.path("rest").path("threads").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 				
//		// Assert - we've got input2 first
//		assertEquals("Listing by last added", "sub2", listedPosts.getPosts().get(0).getSubject());
//		
//		// Act - modify input 1
//		EditPostResource editedInput = new EditPostResource();
//		editedInput.setContent("blar");
//		editedInput.setSubject("blar2");
//		EditPostResourceReturnData editReturnData = service
//		.path("rest").path("post").path("edit")
//		.path(String.valueOf(listedPosts.getPosts().get(1).getId()))
//	    .type(MediaType.APPLICATION_JSON)
//		.header("AuthKey", loginResult.getAuthKey())
//    	.post(EditPostResourceReturnData.class, editedInput); 			
//		assertTrue(editReturnData.isSuccessful());
//		
//		// Assert - we've now got input1 first
//		listedPosts = service
//		.path("rest").path("threads").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 				
//		// Assert - we've got input2 first
//		assertEquals("Listing by last added", "blar2", listedPosts.getPosts().get(0).getSubject());
//	}
//	
//	@SuppressWarnings("serial")
//	@Test
//	public void shouldListThreadsByTagWithLastModifiedContentFirst() {	
//		// Arrange
//		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
//		input1.setTags(new ArrayList<String>(){{add("a");add("b");}});
//		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
//		input2.setTags(new ArrayList<String>(){{add("a");add("b");}});
//		addPost(input1); 
//		addPost(input2); 		
//		// Note: input2 was added last so should be first in the list
//		// Arrange - list them
//		ListPostsResource listedPosts = service
//		.path("rest").path("threads").path("a").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 				
//		// Assert - we've got input2 first
//		assertEquals("Listing by last added", "sub2", listedPosts.getPosts().get(0).getSubject());
//		
//		// Act - modify input 1
//		EditPostResource editedInput = new EditPostResource();
//		editedInput.setContent("blar");
//		editedInput.setSubject("blar2");
//		editedInput.setTags(new ArrayList<String>(){{add("a");add("b");}});
//		EditPostResourceReturnData editReturnData = service
//		.path("rest").path("post").path("edit")
//		.path(String.valueOf(listedPosts.getPosts().get(1).getId()))
//	    .type(MediaType.APPLICATION_JSON)
//		.header("AuthKey", loginResult.getAuthKey())
//    	.post(EditPostResourceReturnData.class, editedInput); 			
//		assertTrue(editReturnData.isSuccessful());
//		
//		// Assert - we've now got input1 first
//		listedPosts = service
//		.path("rest").path("threads").path("a").path("0").path("10")
//		.header("AuthKey", loginResult.getAuthKey())
//    	.get(ListPostsResource.class); 				
//		// Assert - we've got input2 first
//		assertEquals("Listing by last added", "blar2", listedPosts.getPosts().get(0).getSubject());
//	}

}

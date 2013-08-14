package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
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
		addPost(input); 
		addPost(input1); 
		addPost(input2); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("threads").path("0").path("10")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals(2, returnData.getPosts().size());
		assertTrue(returnData.getPosts().get(0).getId()!=0);
		assertTrue(returnData.getPosts().get(1).getId()!=0);
		assertEquals("sub", returnData.getPosts().get(0).getSubject());
		assertEquals("cont", returnData.getPosts().get(0).getContent());
		assertEquals("t", returnData.getPosts().get(0).getThreadId());
		assertEquals("sub1", returnData.getPosts().get(1).getSubject());
		assertEquals("cont1", returnData.getPosts().get(1).getContent());		
		assertEquals("other", returnData.getPosts().get(1).getThreadId());		
	}
	
	@Test
	public void shouldListThreadsByPostLastEnteredWithLimit() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", "t");
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		addPost(input); 
		addPost(input1); 
		addPost(input2); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("threads").path("1").path("1")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals(1, returnData.getPosts().size());
		assertEquals("sub1", returnData.getPosts().get(0).getSubject());
		assertEquals("cont1", returnData.getPosts().get(0).getContent());
		assertEquals("other", returnData.getPosts().get(0).getThreadId());
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
		addPost(input0); 
		addPost(input); 
		addPost(input1); 
		addPost(input2); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("threads").path("onetag").path("0").path("10")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals(2, returnData.getPosts().size());
		assertEquals("sub", returnData.getPosts().get(0).getSubject());
		assertEquals("cont", returnData.getPosts().get(0).getContent());
		assertEquals("t", returnData.getPosts().get(0).getThreadId());
		assertEquals("onetag", returnData.getPosts().get(0).getTags().get(1));
		
		assertEquals("x", returnData.getPosts().get(1).getSubject());
		assertEquals("x", returnData.getPosts().get(1).getContent());
		assertEquals("x", returnData.getPosts().get(1).getThreadId());
		assertEquals("onetag", returnData.getPosts().get(1).getTags().get(0));
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
		addPost(input0); 
		addPost(input); 
		addPost(input1); 
		addPost(input2); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("threads").path("onetag").path("1").path("1")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals(1, returnData.getPosts().size());
		assertEquals("x", returnData.getPosts().get(0).getSubject());
		assertEquals("x", returnData.getPosts().get(0).getContent());
		assertEquals("x", returnData.getPosts().get(0).getThreadId());
		assertEquals("onetag", returnData.getPosts().get(0).getTags().get(0));
	}

	@Test
	public void shouldListThreadsWithThreadWithLastModifiedContentFirst() {	
		// Arrange
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		addPost(input1); 
		addPost(input2); 		
		// Note: input2 was added last so should be first in the list
		// Arrange - list them
		ListPostsResource listedPosts = service
		.path("rest").path("threads").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "sub2", listedPosts.getPosts().get(0).getSubject());
		
		// Act - modify input 1
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("blar");
		editedInput.setSubject("blar2");
		EditPostResourceReturnData editReturnData = service
		.path("rest").path("post").path("edit")
		.path(String.valueOf(listedPosts.getPosts().get(1).getId()))
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.post(EditPostResourceReturnData.class, editedInput); 			
		assertTrue(editReturnData.isSuccessful());
		
		// Assert - we've now got input1 first
		listedPosts = service
		.path("rest").path("threads").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "blar2", listedPosts.getPosts().get(0).getSubject());
	}
	
	@SuppressWarnings("serial")
	@Test
	public void shouldListThreadsByTagWithThreadWithLastModifiedContentFirst() {	
		// Arrange
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		input1.setTags(new ArrayList<String>(){{add("a");add("b");}});
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		input2.setTags(new ArrayList<String>(){{add("a");add("b");}});
		addPost(input1); 
		addPost(input2); 		
		// Note: input2 was added last so should be first in the list
		// Arrange - list them
		ListPostsResource listedPosts = service
		.path("rest").path("threads").path("a").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "sub2", listedPosts.getPosts().get(0).getSubject());
		
		// Act - modify input 1
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("blar");
		editedInput.setSubject("blar2");
		editedInput.setTags(new ArrayList<String>(){{add("a");add("b");}});
		EditPostResourceReturnData editReturnData = service
		.path("rest").path("post").path("edit")
		.path(String.valueOf(listedPosts.getPosts().get(1).getId()))
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.post(EditPostResourceReturnData.class, editedInput); 			
		assertTrue(editReturnData.isSuccessful());
		
		// Assert - we've now got input1 first
		listedPosts = service
		.path("rest").path("threads").path("a").path("0").path("10")
		.header("AuthKey", loginResult.getAuthKey())
    	.get(ListPostsResource.class); 				
		// Assert - we've got input2 first
		assertEquals("Listing by last added", "blar2", listedPosts.getPosts().get(0).getSubject());
	}

	public void addPost(AddPostResourceInput input2) {
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input2);
	}		
}

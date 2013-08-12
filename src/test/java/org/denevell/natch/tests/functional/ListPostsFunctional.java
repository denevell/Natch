package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class ListPostsFunctional {
	
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void shouldListByCreationDate() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		List asList = Arrays.asList(new String[] {"tagy", "tagy1"});
		input.setTags(asList);
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input1); 
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input2); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post").path("0").path("10")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals(3, returnData.getPosts().size());
		assertTrue(returnData.getPosts().get(0).getId()!=0);
		assertTrue(returnData.getPosts().get(1).getId()!=0);
		assertTrue(returnData.getPosts().get(2).getId()!=0);
		assertEquals("sub2", returnData.getPosts().get(0).getSubject());
		assertEquals("cont2", returnData.getPosts().get(0).getContent());
		assertEquals("sub1", returnData.getPosts().get(1).getSubject());
		assertEquals("cont1", returnData.getPosts().get(1).getContent());
		assertEquals("sub", returnData.getPosts().get(2).getSubject());
		assertEquals("cont", returnData.getPosts().get(2).getContent());
		assertEquals("tagy1", returnData.getPosts().get(2).getTags().get(1));
	}
	
	@Test
	public void shouldListByCreationDateWithLimit() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input1); 
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input2); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post").path("1").path("1")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals(1, returnData.getPosts().size());
		assertTrue(returnData.getPosts().get(0).getId()!=0);
		assertEquals("sub1", returnData.getPosts().get(0).getSubject());
		assertEquals("cont1", returnData.getPosts().get(0).getContent());
	}	
	
	@Test
	public void shouldHtmlEscapeSubjectContentTags() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("<hi>", "<there>");
		ArrayList<String> tags = new ArrayList<String>(){{ add("<again>"); add("<hmm>"); }};
		input.setTags(tags);
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post").path("0").path("10")
    	.get(ListPostsResource.class); 		
		
		// Assert
		assertEquals("&lt;hi&gt;", returnData.getPosts().get(0).getSubject());
		assertEquals("&lt;there>", returnData.getPosts().get(0).getContent());
		assertEquals("&lt;again&gt;", returnData.getPosts().get(0).getTags().get(0));
	}		
	
	@Test
	public void shouldFindPost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		// Get Id
		ListPostsResource returnList = service
		.path("rest").path("post").path("0").path("10")
    	.get(ListPostsResource.class); 		
		long id = returnList.getPosts().get(0).getId();
		
		// Act
		PostResource returnData = service
		.path("rest").path("post").path(String.valueOf(id))
    	.get(PostResource.class); 
		
		// Assert
		assertNotNull(returnData);
		assertEquals("sub", returnData.getSubject());
	}		
	
	@Test
	public void shouldListByModificationDateWithNonSpecifiedThreadId() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post").path("0").path("10")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertNotNull(returnData.getPosts().get(0).getThreadId());
	}	
	
	@Test
	public void shouldListByModificationDateWithSpecifiedThreadId() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		input.setThreadId("threadId");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post").path("0").path("10")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals("threadId", returnData.getPosts().get(0).getThreadId());
	}	
	
	@Test
	public void shouldPostsListByThreadId() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", "t");
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		AddPostResourceInput input2 = new AddPostResourceInput("sub2", "cont2", "t");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input1); 
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input2); 
		
		// Act
		ThreadResource returnData = service
		.path("rest").path("post").path("t").path("0").path("20")
    	.get(ThreadResource.class); 
		
		// Assert
		assertEquals(2, returnData.getPosts().size());
		assertTrue(returnData.getPosts().get(0).getId()!=0);
		assertTrue(returnData.getPosts().get(1).getId()!=0);
		assertEquals("sub", returnData.getSubject());
		assertEquals("aaron@aaron.com", returnData.getAuthor());
		assertEquals("cont", returnData.getPosts().get(0).getContent());
		assertEquals("t", returnData.getPosts().get(0).getThreadId());
		assertEquals("sub", returnData.getPosts().get(1).getSubject());
		assertEquals("cont2", returnData.getPosts().get(1).getContent());		
		assertEquals("t", returnData.getPosts().get(1).getThreadId());
	}
	
	@Test
	public void shouldListPostsByThreadIdWithLimit() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", "t");
		AddPostResourceInput input1 = new AddPostResourceInput("sub1", "cont1", "other");
		AddPostResourceInput input2 = new AddPostResourceInput("rubbish", "cont2", "t");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input1); 
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input2); 
		
		// Act
		ThreadResource returnData = service
		.path("rest").path("post").path("t").path("1").path("1")
    	.get(ThreadResource.class); 
		
		// Assert
		assertEquals(1, returnData.getPosts().size());
		assertEquals("sub", returnData.getSubject());
		assertEquals("aaron@aaron.com", returnData.getAuthor());
		assertEquals("cont2", returnData.getPosts().get(0).getContent());
		assertEquals("t", returnData.getPosts().get(0).getThreadId());
	}		
	
	@Test
	public void shouldShowBlankOnBadThreadId() {
		// Arrange 
		
		// Act
		try {
			service
			.path("rest").path("post").path("xxxxxxxxxxx").path("0").path("20")
	    	.get(ThreadResource.class); 
		} catch (UniformInterfaceException e) {
			assertEquals(404, e.getResponse().getStatus());
			return;
		} catch(Exception e) {
			assertTrue("Expected 404", false);
			return;
		}
		
		// Assert
		assertTrue("Expected 404", false);
	}	
	
}

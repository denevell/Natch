package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.posts.resources.AddPostResourceInput;
import org.denevell.natch.serv.posts.resources.AddPostResourceReturnData;
import org.denevell.natch.serv.posts.resources.ListPostsResource;
import org.denevell.natch.serv.users.resources.LoginResourceInput;
import org.denevell.natch.serv.users.resources.LoginResourceReturnData;
import org.denevell.natch.serv.users.resources.RegisterResourceInput;
import org.denevell.natch.serv.users.resources.RegisterResourceReturnData;
import org.junit.Before;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

import com.sun.jersey.api.client.WebResource;

public class ListPostsFunctional {
	
	private LoginResourceReturnData loginResult;
	private WebResource service;

	@Before
	public void setup() {
		service = TestUtils.getRESTClient();
		// Delete all users
		service
	    	.path("rest")
	    	.path("testutils")
	    	.delete();	
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
		.path("rest").path("post").path("0").path("1")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals(1, returnData.getPosts().size());
		assertTrue(returnData.getPosts().get(0).getId()!=0);
		assertEquals("sub2", returnData.getPosts().get(0).getSubject());
		assertEquals("cont2", returnData.getPosts().get(0).getContent());
	}	
	
	@Test
	public void shouldHtmlEscapeSubjectAndContent() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("<hi>", "<there>");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post")
    	.get(ListPostsResource.class); 		
		
		// Assert
		assertEquals("&lt;hi&gt;", returnData.getPosts().get(0).getSubject());
		assertEquals("&lt;there&gt;", returnData.getPosts().get(0).getContent());
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
		.path("rest").path("post")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertNotNull(returnData.getPosts().get(0).getThreadId());
	}	
	
	@Test
	public void shouldListByModificationDateWithSpecifiedThreadId() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		input.setThread("threadId");
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals("threadId", returnData.getPosts().get(0).getThreadId());
	}	
	
	@Test
	public void shouldListByThreadId() {
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
		ListPostsResource returnData = service
		.path("rest").path("post").path("t")
    	.get(ListPostsResource.class); 
		
		// Assert
		assertEquals(2, returnData.getPosts().size());
		assertTrue(returnData.getPosts().get(0).getId()!=0);
		assertTrue(returnData.getPosts().get(1).getId()!=0);
		assertEquals("sub", returnData.getPosts().get(0).getSubject());
		assertEquals("cont", returnData.getPosts().get(0).getContent());
		assertEquals("t", returnData.getPosts().get(0).getThreadId());
		assertEquals("sub2", returnData.getPosts().get(1).getSubject());
		assertEquals("cont2", returnData.getPosts().get(1).getContent());		
		assertEquals("t", returnData.getPosts().get(1).getThreadId());
	}
	
	@Test
	public void shouldListThreadsByPostLastEntered() {
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
		ListPostsResource returnData = service
		.path("rest").path("post").path("threads")
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
	
	@SuppressWarnings("unchecked")
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
		service
		.path("rest").path("post").path("add")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input0); 
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
		.path("rest").path("post").path("threads").path("onetag")
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
}

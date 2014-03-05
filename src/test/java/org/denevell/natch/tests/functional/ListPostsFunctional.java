package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.AddPostPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class ListPostsFunctional {
	
	private LoginResourceReturnData loginResult;
	private WebTarget service;
	private WebTarget listThread;
	private AddPostPO addPostPo;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		// Delete all users
		TestUtils.deleteTestDb();
	    new RegisterPO(service).register("aaron@aaron.com", "passy");
		listThread = service.path("rest").path("post").path("thread");
		loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
	    addPostPo = new AddPostPO(service);
	}
	
	@Test
	public void shouldListByCreationDate() {
		// Arrange 
		addPostPo.add("sub", "cont", new String[] {"tagy", "tagy1"}, loginResult.getAuthKey());
		addPostPo.add("sub1", "cont1", loginResult.getAuthKey());
		addPostPo.add("sub2", "cont2", loginResult.getAuthKey());
		
		// Act
		ListPostsResource returnData = listRecentPostsThreads(service); 
		
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
		addPostPo.add("sub", "cont", loginResult.getAuthKey());
		addPostPo.add("sub1", "cont1", loginResult.getAuthKey());
		addPostPo.add("sub2", "cont2", loginResult.getAuthKey());
		
		// Act
		ListPostsResource returnData = service
		.path("rest").path("post").path("1").path("1").request()
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
		addPostPo.add("<hi>", "<there>", new String[] {"<again>", "<hmm>"}, loginResult.getAuthKey());
		
		// Act
		ListPostsResource returnData = listRecentPostsThreads(service); 		
		
		// Assert
		assertEquals("&lt;hi&gt;", returnData.getPosts().get(0).getSubject());
		assertEquals("&lt;there>", returnData.getPosts().get(0).getContent());
		assertEquals("&lt;again&gt;", returnData.getPosts().get(0).getTags().get(0));
	}		
	
	@Test
	public void shouldListByModificationDateWithNonSpecifiedThreadId() {
		// Arrange 
		addPostPo.add("sub", "cont", loginResult.getAuthKey());
		
		ListPostsResource returnData = listRecentPostsThreads(service); 
		
		// Assert
		assertNotNull(returnData.getPosts().get(0).getThreadId());
	}	
	
	@Test
	public void shouldListByModificationDateWithSpecifiedThreadId() {
		// Arrange 
		addPostPo.add("sub", "cont", loginResult.getAuthKey(), "threadId");
		
		ListPostsResource returnData = listRecentPostsThreads(service); 
		
		// Assert
		assertEquals("threadId", returnData.getPosts().get(0).getThreadId());
	}	
	
	@Test
	public void shouldPostsListByThreadId() {
		// Arrange 
		addPostPo.add("sub", "cont", loginResult.getAuthKey(), "t");
		addPostPo.add("sub1", "cont1", loginResult.getAuthKey(), "other");
		addPostPo.add("sub2", "cont2", loginResult.getAuthKey(), "t");
		
		// Act
		ThreadResource returnData = listThread.path("t").path("0").path("20").request()
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
		addPostPo.add("sub", "cont", new String[] {"again", "blar"}, loginResult.getAuthKey(), "t");
		addPostPo.add("sub1", "cont1", loginResult.getAuthKey(), "other");
		addPostPo.add("rubbish", "cont2", loginResult.getAuthKey(), "t");
		
		// Act
		ThreadResource returnData = listThread.path("t").path("1").path("1").request()
    	.get(ThreadResource.class); 
		
		// Assert
		assertEquals(1, returnData.getPosts().size());
		assertEquals(2, returnData.getNumPosts());
		assertEquals("again", returnData.getTags().get(0));
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
			.path("rest").path("post").path("xxxxxxxxxxx").path("0").path("20").request()
	    	.get(ThreadResource.class); 
		} catch (WebApplicationException e) {
			assertEquals(404, e.getResponse().getStatus());
			return;
		} catch(Exception e) {
			assertTrue("Expected 404", false);
			return;
		}
		
		// Assert
		assertTrue("Expected 404", false);
	}	

    public static ListPostsResource listRecentPostsThreads(WebTarget service) {
        ListPostsResource returnData = service
		.path("rest").path("post").path("0").path("10").request()
    	.get(ListPostsResource.class);
        return returnData;
    }
	
	
}

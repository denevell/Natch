package org.denevell.natch.tests.unit.threads;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.io.threads.ListThreadsResource;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.ThreadsListModel;
import org.denevell.natch.model.interfaces.ThreadsListModel.ThreadsAndNumTotalThreads;
import org.denevell.natch.serv.thread.ListThreadsRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class ListThreadsResourceTests {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private ListThreadsRequest resource;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ThreadsListModel threadModel;

	@Before
	public void setup() {
		response = mock(HttpServletResponse.class);
		request = mock(HttpServletRequest.class);
		threadModel = mock(ThreadsListModel.class);
		resource = new ListThreadsRequest(threadModel, request, response);
	}
	
	@Test
	public void shouldListThreads() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		PostEntity postEntity1 = new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", "t");
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		ThreadEntity threadEntity = new ThreadEntity(postEntity, Arrays.asList(new PostEntity[] { postEntity } ));
		threadEntity.setId("400");
		threads.add(threadEntity);
		threads.add(new ThreadEntity(postEntity1, Arrays.asList(new PostEntity[] { postEntity1 } )));
		ThreadsAndNumTotalThreads ret = new ThreadsAndNumTotalThreads(threads, 0);
		when(threadModel.list(null, 0, 0)).thenReturn(ret);
		
		// Act
		ListThreadsResource result = resource.listThreads(0, 0);
		
		// Assert
		assertEquals(2, result.getThreads().size());
		assertEquals("400", result.getThreads().get(0).getId());
		assertEquals(1, result.getThreads().get(0).getCreation());
		assertEquals(1, result.getThreads().get(0).getModification());
		assertEquals("u1", result.getThreads().get(0).getAuthor());
		assertEquals("s1", result.getThreads().get(0).getSubject());
		assertEquals(2, result.getThreads().get(1).getCreation());
		assertEquals(2, result.getThreads().get(1).getModification());
		assertEquals("u2", result.getThreads().get(1).getAuthor());
		assertEquals("s2", result.getThreads().get(1).getSubject());
	}	
	
	@Test
	public void shouldntListThreadsWhenWithAnInitialPostAsNull() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		PostEntity postEntity1 = new PostEntity(new UserEntity("u2", ""), 1, 1, "s2", "c2", "t");
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		threads.add(new ThreadEntity(null, Arrays.asList(new PostEntity[] { postEntity } )));
		threads.add(new ThreadEntity(postEntity1, Arrays.asList(new PostEntity[] { postEntity1 } )));
		ThreadsAndNumTotalThreads ret = new ThreadsAndNumTotalThreads(threads, 0);
		when(threadModel.list(null, 0, 0)).thenReturn(ret);
		
		// Act
		ListThreadsResource result = resource.listThreads(0, 0);
		
		// Assert
		assertEquals(1, result.getThreads().size());
	}		
	
	@Test
	public void shouldListThreadsByTag() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		PostEntity postEntity1 = new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", "t");
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		ThreadEntity threadEntity = new ThreadEntity(postEntity, Arrays.asList(new PostEntity[] { postEntity } ));
		threadEntity.setId("400");
		threads.add(threadEntity);
		threads.add(new ThreadEntity(postEntity1, Arrays.asList(new PostEntity[] { postEntity1 } )));
		ThreadsAndNumTotalThreads ret = new ThreadsAndNumTotalThreads(threads, 0);
		when(threadModel.list("tagy", 0, 0)).thenReturn(ret);
		
		// Act
		ListThreadsResource result = resource.listThreadsByTag("tagy", 0, 0);
		
		// Assert
		assertEquals(2, result.getThreads().size());
		assertEquals("400", result.getThreads().get(0).getId());
		assertEquals(1, result.getThreads().get(0).getCreation());
		assertEquals(1, result.getThreads().get(0).getModification());
		assertEquals("u1", result.getThreads().get(0).getAuthor());
		assertEquals("s1", result.getThreads().get(0).getSubject());
		assertEquals(2, result.getThreads().get(1).getCreation());
		assertEquals(2, result.getThreads().get(1).getModification());
		assertEquals("u2", result.getThreads().get(1).getAuthor());
		assertEquals("s2", result.getThreads().get(1).getSubject());
	}		
	
}
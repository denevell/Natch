package org.denevell.natch.tests.unit.threads;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.threads.ThreadsResource;
import org.denevell.natch.serv.threads.ThreadModel;
import org.denevell.natch.serv.threads.ThreadsREST;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class ListThreadsResourceTests {
	
	private ThreadModel threadModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private ThreadsREST resource;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Before
	public void setup() {
		threadModel = mock(ThreadModel.class);
		response = mock(HttpServletResponse.class);
		request = mock(HttpServletRequest.class);
		resource = new ThreadsREST(threadModel, request, response);
	}
	
	@SuppressWarnings("serial")
	@Test
	public void shouldListThreads() throws IOException {
		// Arrange
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		UserEntity user = new UserEntity("u", "p");
		ThreadEntity threadEntity = new ThreadEntity("s", "c", new ArrayList<String>(){{add("t");}}, user);
		threadEntity.setCreated(1);
		threadEntity.setModified(1);
		threadEntity.setId(123);
		threads.add(threadEntity);
		threads.add(new ThreadEntity("s1", "c1", null, user));
		when(threadModel.listThreads(1, 2)).thenReturn(threads);
		when(threadModel.getTotalNumberOfThreads(null)).thenReturn(3l);
		
		// Act
		ThreadsResource result = resource.listThreads(1, 2);
		
		// Assert
		assertEquals(2, result.getThreads().size());
		assertEquals(1, result.getPage());
		assertEquals(2, result.getTotalPages());
		assertEquals(123, result.getThreads().get(0).getId());
		assertEquals(1, result.getThreads().get(0).getCreation());
		assertEquals(1, result.getThreads().get(0).getModification());
		assertEquals("t", result.getThreads().get(0).getTags().get(0));
		assertEquals("u", result.getThreads().get(0).getAuthor());
		assertEquals("s", result.getThreads().get(0).getSubject());
		assertEquals("c", result.getThreads().get(0).getContent());
		assertEquals("s1", result.getThreads().get(1).getSubject());
	}	
	
	@SuppressWarnings("serial")
	@Test
	public void shouldListThreadsNumberOfPagesTest() throws IOException {
		// Arrange
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		UserEntity user = new UserEntity("u", "p");
		ThreadEntity threadEntity = new ThreadEntity("s", "c", new ArrayList<String>(){{add("t");}}, user);
		threadEntity.setCreated(1);
		threadEntity.setModified(1);
		threadEntity.setId(123);
		threads.add(threadEntity);
		threads.add(new ThreadEntity("s1", "c1", null, user));
		when(threadModel.listThreads(1, 2)).thenReturn(threads);
		when(threadModel.getTotalNumberOfThreads(null)).thenReturn(4l);
		
		// Act
		ThreadsResource result = resource.listThreads(1, 2);
		
		// Assert
		assertEquals(2, result.getThreads().size());
		assertEquals(1, result.getPage());
		assertEquals(2, result.getTotalPages());
	}		

	@Test
	public void shouldListThreadsByTag() throws IOException {
		// Arrange
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		UserEntity user = new UserEntity("u", "p");
		ThreadEntity threadEntity = new ThreadEntity("s", "c", null, user);
		threadEntity.setCreated(1);
		threadEntity.setModified(1);
		threadEntity.setId(123);
		threads.add(threadEntity);
		threads.add(new ThreadEntity("s1", "c1", null, user));
		when(threadModel.listThreadsByTag("tagy", 1, 2)).thenReturn(threads);
		when(threadModel.getTotalNumberOfThreads("tagy")).thenReturn(4l);
		
		// Act
		ThreadsResource result = resource.listThreadsByTag("tagy", 1, 2);
		
		// Assert
		assertEquals(2, result.getThreads().size());
		assertEquals(1, result.getPage());
		assertEquals(2, result.getTotalPages());
		assertEquals(123, result.getThreads().get(0).getId());
		assertEquals(1, result.getThreads().get(0).getCreation());
		assertEquals(1, result.getThreads().get(0).getModification());
		assertEquals("u", result.getThreads().get(0).getAuthor());
		assertEquals("s", result.getThreads().get(0).getSubject());
		assertEquals("c", result.getThreads().get(0).getContent());
		assertEquals("s1", result.getThreads().get(1).getSubject());
	}		
	
}
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

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.serv.threads.ThreadModel;
import org.denevell.natch.serv.threads.ThreadsREST;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

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
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldListThreads() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		postEntity.setId(400);
		PostEntity postEntity1 = new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", "t");
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		threads.add(new ThreadEntity(postEntity, Arrays.asList(new PostEntity[] { postEntity } )));
		threads.add(new ThreadEntity(postEntity1, Arrays.asList(new PostEntity[] { postEntity1 } )));
		when(threadModel.listThreads(0, 10)).thenReturn(threads);
		
		// Act
		ListPostsResource result = resource.listThreads(0, 10);
		
		// Assert
		assertEquals(2, result.getPosts().size());
		assertEquals(400, result.getPosts().get(0).getId());
		assertEquals(1, result.getPosts().get(0).getCreation());
		assertEquals(1, result.getPosts().get(0).getModification());
		assertEquals("u1", result.getPosts().get(0).getUsername());
		assertEquals("s1", result.getPosts().get(0).getSubject());
		assertEquals("c1", result.getPosts().get(0).getContent());
		assertEquals(2, result.getPosts().get(1).getCreation());
		assertEquals(2, result.getPosts().get(1).getModification());
		assertEquals("u2", result.getPosts().get(1).getUsername());
		assertEquals("s2", result.getPosts().get(1).getSubject());
		assertEquals("c2", result.getPosts().get(1).getContent());
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldntListThreadsWhenWithAnInitialPostAsNull() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		PostEntity postEntity1 = new PostEntity(new UserEntity("u2", ""), 1, 1, "s2", "c2", "t");
		postEntity.setId(400);
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		threads.add(new ThreadEntity(null, Arrays.asList(new PostEntity[] { postEntity } )));
		threads.add(new ThreadEntity(postEntity1, Arrays.asList(new PostEntity[] { postEntity1 } )));
		when(threadModel.listThreads(0, 10)).thenReturn(threads);
		
		// Act
		ListPostsResource result = resource.listThreads(0, 10);
		
		// Assert
		assertEquals(1, result.getPosts().size());
	}		
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldListThreadsByTag() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		postEntity.setId(400);
		PostEntity postEntity1 = new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", "t");
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		threads.add(new ThreadEntity(postEntity, Arrays.asList(new PostEntity[] { postEntity } )));
		threads.add(new ThreadEntity(postEntity1, Arrays.asList(new PostEntity[] { postEntity1 } )));
		when(threadModel.listThreadsByTag("tagy", 0, 10)).thenReturn(threads);
		
		// Act
		ListPostsResource result = resource.listThreadsByTag("tagy", 0, 10);
		
		// Assert
		assertEquals(2, result.getPosts().size());
		assertEquals(400, result.getPosts().get(0).getId());
		assertEquals(1, result.getPosts().get(0).getCreation());
		assertEquals(1, result.getPosts().get(0).getModification());
		assertEquals("u1", result.getPosts().get(0).getUsername());
		assertEquals("s1", result.getPosts().get(0).getSubject());
		assertEquals("c1", result.getPosts().get(0).getContent());
		assertEquals(2, result.getPosts().get(1).getCreation());
		assertEquals(2, result.getPosts().get(1).getModification());
		assertEquals("u2", result.getPosts().get(1).getUsername());
		assertEquals("s2", result.getPosts().get(1).getSubject());
		assertEquals("c2", result.getPosts().get(1).getContent());
	}		
	
}
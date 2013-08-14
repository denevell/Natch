package org.denevell.natch.tests.unit.threads;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.ThreadFactory;
import org.denevell.natch.serv.threads.ThreadModel;
import org.junit.Before;
import org.junit.Test;

public class AddThreadModelTests {
	
	private ThreadModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private UserEntity userEntity;
	private ThreadEntity genericThread;
	private ThreadFactory threadFactory;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		userEntity = new UserEntity("user", "pass");
		genericThread = mock(ThreadEntity.class);
		when(genericThread.getId()).thenReturn(123l);
		when(entityManager.getTransaction()).thenReturn(trans);
		threadFactory = mock(ThreadFactory.class);
		model = new ThreadModel(factory, entityManager, threadFactory);
	}
	
	@Test
	public void shouldMakeThread() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		when(threadFactory.makeThread(subject, content, null, userEntity))
		.thenReturn(genericThread);
		
		// Act
		String result = model.addThread(userEntity, subject, content, null);
		
		// Assert
		assertEquals("123", result);
		verify(entityManager).persist(genericThread);
	}
/*	
	@Test
	public void shouldMakeThreadOnNullThreadId() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		when(threadFactory.makeThread(genericPost)).thenReturn(genericThread);
		
		// Act
		String result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(PostsModel.ADDED, result);
		verify(entityManager).persist(genericThread);
	}	
	
	@Test
	public void shouldReturnUnknownErrorOnEntityManagerException() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		
		// Act
		String result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(PostsModel.UNKNOWN_ERROR, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakeThreadOnPostFactoryNullResult() {
		// Arrange
		String content = "dsdfdf";
		String subject = "asdfsdf";
		when(adapter.createPost(null, userEntity)).thenReturn(null);
		
		// Act
		String result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(PostsModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakeThreadOnBlanks() {
		// Arrange
		String content = " ";
		String subject = " ";
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		String result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(PostsModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakeThreadOnNulls() {
		// Arrange
		genericPost.setContent(null);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		String result = model.addPost(null, adapter);
		
		// Assert
		assertEquals(PostsModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakeThreadOnBlankSubject() {
		// Arrange
		String content = "sdfsdf";
		String subject = " ";
		genericPost.setContent(content);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		String result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(PostsModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakeThreadOnNullSubject() {
		// Arrange
		String content = "dsfsdf";
		String subject = null;
		genericPost.setContent(content);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		String result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(PostsModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}		
	
	@Test
	public void shouldntMakeThreadOnBlankContent() {
		// Arrange
		String content = " ";
		String subject = "dsfsdf";
		genericPost.setContent(content);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		String result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(PostsModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}	
	
	@Test
	public void shouldntMakeThreadOnNullContent() {
		// Arrange
		String content = null;
		String subject = "dsfsdf";
		genericPost.setContent(content);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		String result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(PostsModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakeThreadOnBlankUser() {
		// Arrange
		String content = "asdf";
		String subject = "dsfsdf";
		genericPost.setContent(content);
		userEntity.setUsername(" ");
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		String result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(PostsModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}	
	
	@Test
	public void shouldntMakeThreadOnNullUser() {
		// Arrange
		String content = "asdf";
		String subject = "dsfsdf";
		genericPost.setContent(content);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		String result = model.addPost(null, adapter);
		
		// Assert
		assertEquals(PostsModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}		*/
}
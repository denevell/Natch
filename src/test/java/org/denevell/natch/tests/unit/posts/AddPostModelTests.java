package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.serv.post.add.AddPostModel;
import org.denevell.natch.serv.post.add.ThreadFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AddPostModelTests {
	
	private AddPostModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private AddPostResourceInput genericPost;
	private UserEntity userEntity;
	private ThreadFactory threadFactory;
	private ThreadEntity genericThread;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		userEntity = new UserEntity("user", "pass");
		genericPost = new AddPostResourceInput();
		genericThread = mock(ThreadEntity.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		threadFactory = mock(ThreadFactory.class);
		model = new AddPostModel(factory, entityManager, threadFactory);
	}
	
	@Ignore
	@Test
	public void shouldMakePost() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		//when(threadFactory.makeThread(genericPost)).thenReturn(genericThread);
		
		// Act
		ThreadEntity result = model.addPost(userEntity, genericPost);
		
		// Assert
		assertNotNull(result);
		verify(entityManager).persist(genericThread);
	}
	
	@Ignore
	@Test
	public void shouldMakePostOnNullThreadId() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		genericPost.setThreadId(null);
		//when(threadFactory.makeThread(genericPost)).thenReturn(genericThread);
		
		// Act
		ThreadEntity result = model.addPost(userEntity, genericPost);
		
		// Assert
		assertNotNull(result);
		verify(entityManager).persist(genericThread);
	}	
	
	@Test
	public void shouldReturnUnknownErrorOnEntityManagerException() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		
		// Act
		ThreadEntity result = model.addPost(userEntity, genericPost);
		
		// Assert
		assertNull(result);
		verify(entityManager, never()).persist(genericThread);
	}
		
}
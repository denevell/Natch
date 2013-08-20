package org.denevell.natch.tests.unit.threads;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.threads.ThreadFactory;
import org.denevell.natch.serv.threads.ThreadModel;
import org.junit.Before;
import org.junit.Test;

public class AddThreadModelTests {
	
	private ThreadModel model;
	private EntityTransaction trans;
	private EntityManager entityManager;
	private UserEntity userEntity;
	private ThreadEntity genericThread;
	private ThreadFactory threadFactory;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		trans = mock(EntityTransaction.class);
		userEntity = new UserEntity("user", "pass");
		genericThread = mock(ThreadEntity.class);
		when(genericThread.getId()).thenReturn(123l);
		when(entityManager.getTransaction()).thenReturn(trans);
		threadFactory = mock(ThreadFactory.class);
		model = new ThreadModel(entityManager, threadFactory);
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

	@Test
	public void shouldReturnUnknownErrorOnEntityManagerException() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		
		// Act
		String result = model.addThread(userEntity, subject, content, null);
		
		// Assert
		assertEquals(ThreadModel.UNKNOWN_ERROR, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakeThreadOnBlanks() {
		// Arrange
		String content = " ";
		String subject = " ";
		
		// Act
		String result = model.addThread(userEntity, subject, content, null);
		
		// Assert
		assertEquals(ThreadModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakeThreadOnNulls() {
		// Arrange
		String content = null;
		String subject = null;
		
		// Act
		String result = model.addThread(userEntity, subject, content, null);
		
		// Assert
		assertEquals(ThreadModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakeThreadOnBlankUser() {
		// Arrange
		String content = "dfsdf";
		String subject = "dsfsdf";
		userEntity.setUsername(" ");
		
		// Act
		String result = model.addThread(userEntity, subject, content, null);
		
		// Assert
		assertEquals(ThreadModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}	

	@Test
	public void shouldntMakeThreadOnNullUser() {
		// Arrange
		String content = "dfsdf";
		String subject = "dsfsdf";
		userEntity = null;
		
		// Act
		String result = model.addThread(userEntity, subject, content, null);
		
		// Assert
		assertEquals(ThreadModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}	

}
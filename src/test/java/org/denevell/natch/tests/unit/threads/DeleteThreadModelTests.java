package org.denevell.natch.tests.unit.threads;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.ThreadsModel;
import org.denevell.natch.serv.posts.ThreadFactory;
import org.denevell.natch.serv.threads.ThreadModel;
import org.junit.Before;
import org.junit.Test;

public class DeleteThreadModelTests {
	
	private ThreadModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private ThreadFactory threadFactory;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		threadFactory = mock(ThreadFactory.class);
		model = spy(new ThreadModel(factory, entityManager, threadFactory));
	}
	
	@Test
	public void shouldDeletePost() {
		// Arrange
		long num = 1;
		ThreadEntity thread = new ThreadEntity();
		thread.setUser(new UserEntity("this_person", null));
		returnThreadFromEntityManager(thread);				
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		String result = model.delete(userEntity, num);
		
		// Verify
		assertEquals(ThreadsModel.DELETED, result);
		verify(entityManager).remove(thread);
	}

	public void returnThreadFromEntityManager(ThreadEntity thread) {
		@SuppressWarnings("unchecked")
		TypedQuery<ThreadEntity> q = mock(TypedQuery.class);
		when(entityManager.createNamedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID, ThreadEntity.class)).thenReturn(q);
		when(q.getSingleResult()).thenReturn(thread);
	}
	
	@Test
	public void shouldReturnUnAuthorised() {
		// Arrange
		long num = 1;
		ThreadEntity post = new ThreadEntity();
		post.setUser(new UserEntity("that_person", null));
		returnThreadFromEntityManager(post);				
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		String result = model.delete(userEntity, num);
		
		// Verify
		assertEquals(ThreadsModel.NOT_YOURS_TO_DELETE, result);
		verify(entityManager, never()).remove(post);
	}
	
	@Test
	public void shouldReturnNotFound() {
		// Arrange
		long num = 1;
		returnThreadFromEntityManager(null);				
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		String result = model.delete(userEntity, num);
		
		// Verify
		assertEquals(ThreadsModel.DOESNT_EXIST, result);
	}
	
}
package org.denevell.natch.tests.unit.threads;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.serv.threads.ThreadFactory;
import org.denevell.natch.serv.threads.ThreadModel;
import org.junit.Before;
import org.junit.Test;

public class ListThreadsModelTests {
	
	private ThreadModel model;
	private EntityTransaction trans;
	private EntityManager entityManager;
	private TypedQuery<ThreadEntity> threadQueryResults;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		trans = mock(EntityTransaction.class);
		threadQueryResults = mock(TypedQuery.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		ThreadFactory threadModel = mock(ThreadFactory.class);
		model = new ThreadModel(entityManager, threadModel);
	}
	
	@Test
	public void shouldReturnListOfThreads() {
		// Arrange
		when(entityManager.createNamedQuery(ThreadEntity.NAMED_QUERY_LIST_THREADS, ThreadEntity.class)).thenReturn(threadQueryResults);
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		threads.add(new ThreadEntity());
		threads.add(new ThreadEntity());
		when(threadQueryResults.getResultList()).thenReturn(threads);
		
		// Act
		List<ThreadEntity> result = model.listThreads(0, 10);
		
		// Assert
		assertEquals(2, result.size());
	}

	@Test
	public void shouldntReturnListThread() {
		// Arrange
		when(entityManager.createNamedQuery(ThreadEntity.NAMED_QUERY_LIST_THREADS, ThreadEntity.class)).thenReturn(threadQueryResults);
		when(threadQueryResults.getResultList()).thenReturn(null);
		
		// Act
		List<ThreadEntity> result = model.listThreads(0, 10);
		
		// Assert
		assertEquals(0, result.size());
	}		

	@Test
	public void shouldReturnThreadsByTag() {
		// Arrange
		when(entityManager.createNamedQuery(ThreadEntity.NAMED_QUERY_LIST_THREADS_BY_TAG, ThreadEntity.class)).thenReturn(threadQueryResults);
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		threads.add(new ThreadEntity());
		threads.add(new ThreadEntity());
		when(threadQueryResults.getResultList()).thenReturn(threads);
		
		// Act
		List<ThreadEntity> result = model.listThreadsByTag("tagy", 0, 10);
		
		// Assert
		assertEquals(2, result.size());
	}		
	
	@Test
	public void shouldntReturnListOfThreadsByTag() {
		// Arrange
		when(entityManager.createNamedQuery(ThreadEntity.NAMED_QUERY_LIST_THREADS_BY_TAG, ThreadEntity.class)).thenReturn(threadQueryResults);
		when(threadQueryResults.getResultList()).thenReturn(null);
		
		// Act
		List<ThreadEntity> result = model.listThreadsByTag("tagy", 0, 10);
		
		// Assert
		assertEquals(0, result.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetNumOfPost() {
		// Arrange
		TypedQuery<Long> q = mock(TypedQuery.class);
		when(q.getSingleResult()).thenReturn(1l);
		when(entityManager.createNamedQuery(ThreadEntity.NAMED_QUERY_FIND_NUMBER_OF_THREADS, Long.class)).thenReturn(q);
		
		// Act
		long result = model.getTotalNumberOfThreads(null);
		
		// Assert
		assertEquals(1, result);
	}	

	@Test
	public void shouldntGetNumOfPost() {
		// Arrange
		when(entityManager.createNamedQuery(ThreadEntity.NAMED_QUERY_FIND_NUMBER_OF_THREADS, Long.class))
		.thenThrow(new RuntimeException());
		
		// Act
		long result = model.getTotalNumberOfThreads(null);
		
		// Assert
		assertEquals(-1, result);
	}	
}
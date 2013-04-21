package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.ThreadFactory;
import org.junit.Before;
import org.junit.Test;

public class ListPostsModelTests {
	
	private PostsModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private TypedQuery<PostEntity> queryResults;
	private ThreadFactory threadFactory;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		queryResults = mock(TypedQuery.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		threadFactory = mock(ThreadFactory.class);
		model = new PostsModel(factory, entityManager, threadFactory);
	}
	
	@Test
	public void shouldReturnListOfPosts() {
		// Arrange
		when(entityManager.createNamedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE, PostEntity.class)).thenReturn(queryResults);
		List<PostEntity> posts = new ArrayList<PostEntity>();
		PostEntity postEntity = new PostEntity(null, 1, 1, "s1", null, null);
		postEntity.setTags(Arrays.asList("tag", "tag1"));
		postEntity.setId(400);
		posts.add(postEntity);
		posts.add(new PostEntity(null, 2, 2, "s2", null, null));
		when(queryResults.getResultList()).thenReturn(posts);
		
		// Act
		List<PostEntity> result = model.listByModificationDate();
		
		// Assert
		assertEquals(2, result.size());
		assertEquals(400, result.get(0).getId());
		assertEquals(1l, result.get(0).getModified());
		assertEquals("s1", result.get(0).getSubject());
		assertEquals("tag1", result.get(0).getTags().get(1));
		assertEquals(2l, result.get(1).getModified());
		assertEquals("s2", result.get(1).getSubject());
	}
	
	@Test
	public void shouldReturnEmptyList() {
		// Arrange
		when(entityManager.createNamedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE, PostEntity.class)).thenReturn(queryResults);
		List<PostEntity> posts = new ArrayList<PostEntity>();
		when(queryResults.getResultList()).thenReturn(posts);
		
		// Act
		List<PostEntity> result = model.listByModificationDate();
		
		// Assert
		assertEquals(0, result.size());
	}
	
	@Test
	public void shouldReturnEmptyListOnNull() {
		// Arrange
		when(entityManager.createNamedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE, PostEntity.class)).thenReturn(queryResults);
		when(queryResults.getResultList()).thenReturn(null);
		
		// Act
		List<PostEntity> result = model.listByModificationDate();
		
		// Assert
		assertEquals(0, result.size());
	}
	
	@Test
	public void shouldReturnListOfPostsWithThreadId() {
		// Arrange
		when(entityManager.createNamedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE, PostEntity.class)).thenReturn(queryResults);
		List<PostEntity> posts = new ArrayList<PostEntity>();
		PostEntity postEntity = new PostEntity(null, 1, 1, "s1", "c1", "threadId");
		postEntity.setId(400);
		posts.add(postEntity);
		when(queryResults.getResultList()).thenReturn(posts);
		
		// Act
		List<PostEntity> result = model.listByModificationDate();
		
		// Assert
		assertEquals("threadId", result.get(0).getThreadId());
	}	
	
	@Test
	public void shouldReturnListOfPostsByTheadId() {
		// Arrange
		when(entityManager.createNamedQuery(PostEntity.NAMED_QUERY_FIND_BY_THREADID, PostEntity.class)).thenReturn(queryResults);
		List<PostEntity> posts = new ArrayList<PostEntity>();
		posts.add(new PostEntity(null, 2, 2, "s1", "c1", "t"));
		posts.add(new PostEntity(null, 3, 3, "s2", "c2", "t"));
		when(queryResults.getResultList()).thenReturn(posts);
		
		// Act
		List<PostEntity> result = model.listByThreadId("t");
		
		// Assert
		assertEquals(2, result.size());
	}	
	
	@Test
	public void shouldReturnListOfPostsByGroupedByThread() {
		// Arrange
		when(entityManager.createNativeQuery(PostEntity.NATIVE_QUERY_FIND_THREADS, PostEntity.class)).thenReturn(queryResults);
		List<PostEntity> posts = new ArrayList<PostEntity>();
		posts.add(new PostEntity(null, 2, 2, "s1", "c1", "t"));
		posts.add(new PostEntity(null, 3, 3, "s2", "c2", "c"));
		when(queryResults.getResultList()).thenReturn(posts);
		
		// Act
		List<PostEntity> result = model.listThreads();
		
		// Assert
		assertEquals(2, result.size());
	}		
	
}
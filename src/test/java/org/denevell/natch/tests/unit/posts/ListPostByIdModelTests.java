package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.serv.post.show.ShowPostModel;
import org.junit.Before;
import org.junit.Test;

public class ListPostByIdModelTests {
	
	private ShowPostModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		model = spy(new ShowPostModel(factory, entityManager));
	}
	
	@Test
	public void shouldFindPost() {
		// Arrange
		List<PostEntity> list = new ArrayList<PostEntity>();
		PostEntity entity = new PostEntity();
		list.add(entity);
		@SuppressWarnings("unchecked")
		TypedQuery<PostEntity> query = mock(TypedQuery.class);
		when(query.setParameter("id", 1l)).thenReturn(query);
		when(query.getResultList()).thenReturn(list);
		when(entityManager.createNamedQuery(PostEntity.NAMED_QUERY_FIND_BY_ID, PostEntity.class)).thenReturn(query);
		
		// Act
		PostEntity result = model.findPostById(1l);
		
		// Verify
		assertNotNull(result);
	}

}
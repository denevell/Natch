package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostFactory;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsModel.EditPostResult;
import org.junit.Before;
import org.junit.Test;

public class EditPostModelTests {
	
	private PostsModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private PostFactory postFactory;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		postFactory = mock(PostFactory.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		model = spy(new PostsModel(factory, entityManager, postFactory));
	}
	
	@Test
	public void shouldEditPost() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUser(new UserEntity("this_person", null));
		doReturn(post).when(model).findPostById(num);
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		EditPostResult result = model.edit(userEntity, num, post);
		
		// Verify
		assertEquals(EditPostResult.EDITED, result);
	}
	
	@Test
	public void shouldReturnUnAuthorised() {
	}
	
	@Test
	public void shouldReturnNotFound() {
	}
	
	@Test
	public void shouldReturnUnknownErrorOnException() {
	}
	
	@Test
	public void shouldReturnUnknownErorrOnNullUser() {
	}
}
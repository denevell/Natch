package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.post.delete.DeletePostModel;
import org.junit.Before;
import org.junit.Test;

public class DeletePostModelTests {
	
	private DeletePostModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		model = spy(new DeletePostModel(factory, entityManager));
	}
	
	@Test
	public void shouldDeletePost() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setThreadId("1");
		post.setUser(new UserEntity("this_person", null));
		doReturn(post).when(model).findPostById(num);
		ThreadEntity thread = new ThreadEntity();
		thread.setPosts(new ArrayList<PostEntity>());
		doReturn(thread).when(model).findThreadById("1");
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		String result = model.delete(userEntity, num);
		
		// Verify
		assertEquals(DeletePostModel.DELETED, result);
	}
	
	@Test
	public void shouldReturnUnAuthorised() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUser(new UserEntity("that_person", null));
		doReturn(post).when(model).findPostById(num);
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");

		// Act
		String result = model.delete(userEntity, num);
		
		// Verify
		assertEquals(DeletePostModel.NOT_YOURS_TO_DELETE, result);
	}

	@Test
	public void shouldAllowAdminToDelete() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUser(new UserEntity("that_person", null));
		post.setThreadId("1");
		doReturn(post).when(model).findPostById(num);
		ThreadEntity thread = new ThreadEntity();
		thread.setPosts(new ArrayList<PostEntity>());
		doReturn(thread).when(model).findThreadById("1");
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		userEntity.setAdmin(true);
		
		// Act
		String result = model.delete(userEntity, num);
		
		// Verify
		assertEquals(DeletePostModel.DELETED, result);
	}
	
	@Test
	public void shouldReturnNotFound() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUser(new UserEntity("this_person", null));
		doReturn(null).when(model).findPostById(num);
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		String result = model.delete(userEntity, num);
		
		// Verify
		assertEquals(DeletePostModel.DOESNT_EXIST, result);
	}
	
	@Test
	public void shouldReturnUnknownErorrOnNullUser() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUser(new UserEntity("this_person", null));
		doThrow(new RuntimeException()).when(model).findPostById(num);
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		String result = model.delete(null, num);
		
		// Verify
		assertEquals(DeletePostModel.UNKNOWN_ERROR, result);
	}
}
package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostFactory;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsModel.AddPostResult;
import org.junit.Before;
import org.junit.Test;

public class AddPostModelTests {
	
	private PostsModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private PostFactory postFactory;
	private PostEntity genericPost;
	private UserEntity userEntity;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		postFactory = mock(PostFactory.class);
		userEntity = new UserEntity("user", "pass");
		genericPost = new PostEntity(userEntity, 0, 0, "", "", "");
		when(entityManager.getTransaction()).thenReturn(trans);
		model = new PostsModel(factory, entityManager, postFactory);
	}
	
	@Test
	public void shouldMakePost() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.ADDED, result);
		verify(entityManager).persist(genericPost);
	}
	
	@Test
	public void shouldReturnUnknownErrorOnEntityManagerException() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(genericPost);
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.UNKNOWN_ERROR, result);
		verify(entityManager, never()).persist(genericPost);
	}
	
	@Test
	public void shouldntMakePostOnPostFactoryNullResult() {
		// Arrange
		String content = " ";
		String subject = " ";
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(null);
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericPost);
	}
	
	@Test
	public void shouldntMakePostOnBlanks() {
		// Arrange
		String content = " ";
		String subject = " ";
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericPost);
	}
	
	@Test
	public void shouldntMakePostOnNulls() {
		// Arrange
		String content = null;
		String subject = null;
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(null, subject, content);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericPost);
	}
	
	@Test
	public void shouldntMakePostOnBlankSubject() {
		// Arrange
		String content = "sdfsdf";
		String subject = " ";
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericPost);
	}
	
	@Test
	public void shouldntMakePostOnNullSubject() {
		// Arrange
		String content = "dsfsdf";
		String subject = null;
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericPost);
	}		
	
	@Test
	public void shouldntMakePostOnBlankContent() {
		// Arrange
		String content = " ";
		String subject = "dsfsdf";
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericPost);
	}	
	
	@Test
	public void shouldntMakePostOnNullContent() {
		// Arrange
		String content = null;
		String subject = "dsfsdf";
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericPost);
	}
	
	@Test
	public void shouldntMakePostOnBlankUser() {
		// Arrange
		String content = "asdf";
		String subject = "dsfsdf";
		userEntity.setUsername(" ");
		when(postFactory.createPost(userEntity, subject, content)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericPost);
	}	
	
	@Test
	public void shouldntMakePostOnNullUser() {
		// Arrange
		String content = "asdf";
		String subject = "dsfsdf";
		when(postFactory.createPost(null, subject, content)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, subject, content);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericPost);
	}		
}
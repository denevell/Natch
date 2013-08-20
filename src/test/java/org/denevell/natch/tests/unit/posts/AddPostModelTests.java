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
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostFactory;
import org.denevell.natch.serv.posts.PostModel;
import org.denevell.natch.serv.threads.ThreadModel;
import org.junit.Before;
import org.junit.Test;

public class AddPostModelTests {
	
	private PostModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private PostEntity genericPost;
	private UserEntity userEntity;
	private PostFactory postFactory;
	private ThreadEntity genericThread;
	private ThreadModel threadModel;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		userEntity = new UserEntity("user", "pass");
		genericThread = new ThreadEntity("" ,"", null, userEntity);
		genericPost = new PostEntity(userEntity, 0, 0, "");
		when(entityManager.getTransaction()).thenReturn(trans);
		postFactory = mock(PostFactory.class);
		threadModel = mock(ThreadModel.class);
		model = new PostModel(factory, entityManager, postFactory, threadModel);
	}
	
	@Test
	public void shouldMakePost() {
		// Arrange
		String content = "Some content";
		genericPost.setContent(content);
		when(postFactory.makePost(content, userEntity)).thenReturn(genericPost);
		when(threadModel.findThreadById(1l)).thenReturn(genericThread);
		
		// Act
		String result = model.addPost(userEntity, content, 1l);
		
		// Assert
		assertEquals(PostModel.ADDED, result);
		verify(entityManager).merge(genericThread);
		verify(postFactory).addPostToThread(genericPost, genericThread);
	}

	@Test
	public void shouldntMakePostOnBadThreadId() {
		// Arrange
		String content = "Some content";
		genericPost.setContent(content);
		when(postFactory.makePost(content, userEntity)).thenReturn(genericPost);
		when(threadModel.findThreadById(1l)).thenReturn(null);
		
		// Act
		String result = model.addPost(userEntity, content, 1l);
		
		// Assert
		assertEquals(PostModel.DOESNT_EXIST, result);
		verify(entityManager, never()).merge(genericThread);
	}
		
	@Test
	public void shouldntMakePostOnBlanks() {
		// Arrange
		String content = "  ";
		genericPost.setContent(content);
		when(postFactory.makePost(content, userEntity)).thenReturn(genericPost);
		when(threadModel.findThreadById(1l)).thenReturn(genericThread);
		
		// Act
		String result = model.addPost(userEntity, content, 1l);
		
		// Assert
		assertEquals(PostModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).merge(genericThread);
	}
	
	@Test
	public void shouldntMakePostOnNulls() {
		// Arrange
		String content = null; 
		genericPost.setContent(content);
		when(postFactory.makePost(content, userEntity)).thenReturn(genericPost);
		when(threadModel.findThreadById(1l)).thenReturn(genericThread);
		
		// Act
		String result = model.addPost(userEntity, content, 1l);
		
		// Assert
		assertEquals(PostModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).merge(genericThread);
	}
		
}
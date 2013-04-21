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
import org.denevell.natch.serv.posts.PostEntityAdapter;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsModel.AddPostResult;
import org.denevell.natch.serv.posts.ThreadFactory;
import org.junit.Before;
import org.junit.Test;

public class AddPostModelTests {
	
	private PostsModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private PostEntity genericPost;
	private UserEntity userEntity;
	private PostEntityAdapter adapter;
	private ThreadFactory threadFactory;
	private ThreadEntity genericThread;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		userEntity = new UserEntity("user", "pass");
		genericPost = new PostEntity(userEntity, 0, 0, "", "", "");
		genericThread = mock(ThreadEntity.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		threadFactory = mock(ThreadFactory.class);
		model = new PostsModel(factory, entityManager, threadFactory);
		adapter = mock(PostEntityAdapter.class);
	}
	
	@Test
	public void shouldMakePost() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		when(threadFactory.makeThread(genericPost)).thenReturn(genericThread);
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.ADDED, result);
		verify(entityManager).persist(genericThread);
	}
	
	@Test
	public void shouldMakePostOnNullThreadId() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		genericPost.setThreadId(null);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		when(threadFactory.makeThread(genericPost)).thenReturn(genericThread);
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.ADDED, result);
		verify(entityManager).persist(genericThread);
	}	
	
	@Test
	public void shouldReturnUnknownErrorOnEntityManagerException() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.UNKNOWN_ERROR, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakePostOnPostFactoryNullResult() {
		// Arrange
		String content = "dsdfdf";
		String subject = "asdfsdf";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(adapter.createPost(null, userEntity)).thenReturn(null);
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakePostOnBlanks() {
		// Arrange
		String content = " ";
		String subject = " ";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakePostOnNulls() {
		// Arrange
		genericPost.setContent(null);
		genericPost.setSubject(null);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(null, adapter);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakePostOnBlankSubject() {
		// Arrange
		String content = "sdfsdf";
		String subject = " ";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakePostOnNullSubject() {
		// Arrange
		String content = "dsfsdf";
		String subject = null;
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}		
	
	@Test
	public void shouldntMakePostOnBlankContent() {
		// Arrange
		String content = " ";
		String subject = "dsfsdf";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}	
	
	@Test
	public void shouldntMakePostOnNullContent() {
		// Arrange
		String content = null;
		String subject = "dsfsdf";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}
	
	@Test
	public void shouldntMakePostOnBlankUser() {
		// Arrange
		String content = "asdf";
		String subject = "dsfsdf";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		userEntity.setUsername(" ");
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(userEntity, adapter);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}	
	
	@Test
	public void shouldntMakePostOnNullUser() {
		// Arrange
		String content = "asdf";
		String subject = "dsfsdf";
		genericPost.setContent(content);
		genericPost.setSubject(subject);
		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
		
		// Act
		AddPostResult result = model.addPost(null, adapter);
		
		// Assert
		assertEquals(AddPostResult.BAD_USER_INPUT, result);
		verify(entityManager, never()).persist(genericThread);
	}		
}
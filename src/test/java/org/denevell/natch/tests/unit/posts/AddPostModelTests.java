package org.denevell.natch.tests.unit.posts;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostModel;
import org.denevell.natch.serv.posts.ThreadFactory;
import org.junit.Before;

public class AddPostModelTests {
	
	private PostModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private PostEntity genericPost;
	private UserEntity userEntity;
	private ThreadFactory threadFactory;
	private ThreadEntity genericThread;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		userEntity = new UserEntity("user", "pass");
		genericPost = new PostEntity(userEntity, 0, 0, "", 0l);
		genericThread = mock(ThreadEntity.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		threadFactory = mock(ThreadFactory.class);
		model = new PostModel(factory, entityManager, threadFactory);
	}
	
//	@Test
//	public void shouldMakePost() {
//		// Arrange
//		String content = "Some content";
//		String subject = "Some subject";
//		genericPost.setContent(content);
//		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
//		when(threadFactory.makeThread(genericPost)).thenReturn(genericThread);
//		
//		// Act
//		String result = model.addPost(userEntity, adapter);
//		
//		// Assert
//		assertEquals(PostsModel.ADDED, result);
//		verify(entityManager).persist(genericThread);
//	}
//		
//	@Test
//	public void shouldntMakePostOnBlanks() {
//		// Arrange
//		String content = " ";
//		String subject = " ";
//		genericPost.setContent(content);
//		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
//		
//		// Act
//		String result = model.addPost(userEntity, adapter);
//		
//		// Assert
//		assertEquals(PostsModel.BAD_USER_INPUT, result);
//		verify(entityManager, never()).persist(genericThread);
//	}
//	
//	@Test
//	public void shouldntMakePostOnNulls() {
//		// Arrange
//		genericPost.setContent(null);
//		when(adapter.createPost(null, userEntity)).thenReturn(genericPost);
//		
//		// Act
//		String result = model.addPost(null, adapter);
//		
//		// Assert
//		assertEquals(PostsModel.BAD_USER_INPUT, result);
//		verify(entityManager, never()).persist(genericThread);
//	}
		
}
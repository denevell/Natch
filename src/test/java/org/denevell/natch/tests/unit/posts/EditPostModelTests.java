package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostFactory;
import org.denevell.natch.serv.posts.PostModel;
import org.denevell.natch.serv.threads.ThreadModel;
import org.junit.Before;
import org.junit.Test;

public class EditPostModelTests {
	
	private PostModel model;
	private EntityTransaction trans;
	private EntityManager entityManager;
	private ThreadModel threadModel;
	private PostFactory postFacory;
	private ThreadEntity genericThread;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		trans = mock(EntityTransaction.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		postFacory = mock(PostFactory.class);
		threadModel = mock(ThreadModel.class);
		genericThread = mock(ThreadEntity.class);
		model = spy(new PostModel(entityManager, postFacory, threadModel));
	}
	
	@Test
	public void shouldEditPost() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity(null, 1, 1, "df");
		post.setUser(new UserEntity("this_person", null));
		doReturn(post).when(model).findPostById(num);
		when(threadModel.findThreadById(num)).thenReturn(genericThread);
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		String result = model.edit(userEntity, num, "new");
		
		// Assert 
		assertEquals(PostModel.EDITED, result);
		verify(postFacory).setThreadAsUpdatedIfPostIsLatest(genericThread, post);
		verify(entityManager).merge(post);
		verify(entityManager).merge(genericThread);
	}
	
	@Test
	public void shouldReturnUnAuthorised() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity(null, 1, 1, "df");
		post.setUser(new UserEntity("this_person", null));
		doReturn(post).when(model).findPostById(num);
		when(threadModel.findThreadById(num)).thenReturn(genericThread);
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("that_person");
		
		// Act
		String result = model.edit(userEntity, num, "new");
		
		// Assert 
		assertEquals(PostModel.NOT_YOURS_TO_DELETE, result);
		verify(entityManager, never()).merge(post);
		verify(entityManager, never()).merge(genericThread);
	}

	@Test
	public void shouldReturnNotFound() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity(null, 1, 1, "df");
		post.setUser(new UserEntity("this_person", null));
		doReturn(null).when(model).findPostById(num);
		when(threadModel.findThreadById(num)).thenReturn(null);
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("that_person");
		
		// Act
		String result = model.edit(userEntity, num, "new");
		
		// Assert 
		assertEquals(PostModel.DOESNT_EXIST, result);
		verify(entityManager, never()).merge(post);
		verify(entityManager, never()).merge(genericThread);
	}

	@Test
	public void shouldReturnBadUserInputOnNull() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity(null, 1, 1, "df");
		post.setUser(new UserEntity("this_person", null));
		doReturn(post).when(model).findPostById(num);
		when(threadModel.findThreadById(num)).thenReturn(genericThread);
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		String result = model.edit(userEntity, num, null);
		
		// Assert 
		assertEquals(PostModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).merge(post);
		verify(entityManager, never()).merge(genericThread);		
	}
	
	@Test
	public void shouldReturnBadUserInputOnBlankSubject() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity(null, 1, 1, "df");
		post.setUser(new UserEntity("this_person", null));
		doReturn(post).when(model).findPostById(num);
		when(threadModel.findThreadById(num)).thenReturn(genericThread);
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("this_person");
		
		// Act
		String result = model.edit(userEntity, num, " ");
		
		// Assert 
		assertEquals(PostModel.BAD_USER_INPUT, result);
		verify(entityManager, never()).merge(post);
		verify(entityManager, never()).merge(genericThread);		
	}
}
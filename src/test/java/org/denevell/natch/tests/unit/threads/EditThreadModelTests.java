package org.denevell.natch.tests.unit.threads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.ThreadFactory;
import org.denevell.natch.serv.threads.ThreadModel;
import org.junit.Before;
import org.junit.Test;

public class EditThreadModelTests {
	
	private ThreadModel model;
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;
	private ThreadFactory threadFactory;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		threadFactory = mock(ThreadFactory.class);
		model = spy(new ThreadModel(factory, entityManager, threadFactory));
	}
	
	@Test
	public void shouldEditPost() {
		// Arrange
		UserEntity userEntity = new UserEntity("this_person", null);
		ThreadEntity thread = new ThreadEntity("s", "c", null, userEntity);
		doReturn(thread).when(model).findThreadById(1);
		@SuppressWarnings("serial")
		ArrayList<String> tags = new ArrayList<String>() {{ add("t"); }};
		long lastModified = thread.getModified();
		
		// Act
		String result = model.edit(userEntity, 1, "x", "xx", tags);
		
		// Assert 
		assertEquals(PostsModel.EDITED, result);
		assertTrue("Have updated modified date stamp", thread.getModified()>lastModified);
		assertEquals("x", thread.getSubject());
		assertEquals("xx", thread.getContent());
		assertEquals(tags, thread.getTags());
	}
	
	@Test
	public void shouldReturnUnAuthorised() {
		// Arrange
		UserEntity userEntity = new UserEntity("this_person", null);
		UserEntity otherUserEntity = new UserEntity("that_person", null);
		ThreadEntity thread = new ThreadEntity("s", "c", null, userEntity);
		doReturn(thread).when(model).findThreadById(1);
		
		// Act
		String result = model.edit(otherUserEntity, 1, "x", "x", null);
		
		// Assert 
		assertEquals(PostsModel.NOT_YOURS_TO_DELETE, result);
	}

	@Test
	public void shouldReturnNotFound() {
		// Arrange
		UserEntity otherUserEntity = new UserEntity("that_person", null);
		doReturn(null).when(model).findThreadById(1);
		
		// Act
		String result = model.edit(otherUserEntity, 1, "x", "x", null);
		
		// Assert 
		assertEquals(PostsModel.DOESNT_EXIST, result);
	}

	@Test
	public void shouldReturnBadUserInputOnBlanks() {
		// Arrange
		UserEntity userEntity = new UserEntity("this_person", null);
		ThreadEntity thread = new ThreadEntity("s", "c", null, userEntity);
		doReturn(thread).when(model).findThreadById(1);
		
		// Act
		String result = model.edit(userEntity, 1, " ", " ", null);
		
		// Assert 
		assertEquals(PostsModel.BAD_USER_INPUT, result);
	}
	
	@Test
	public void shouldReturnBadUserInputOnNull() {
		// Arrange
		UserEntity userEntity = new UserEntity("this_person", null);
		ThreadEntity thread = new ThreadEntity("s", "c", null, userEntity);
		doReturn(thread).when(model).findThreadById(1);
		
		// Act
		String result = model.edit(userEntity, 1, null, null, null);
		
		// Assert 
		assertEquals(PostsModel.BAD_USER_INPUT, result);
	}

}
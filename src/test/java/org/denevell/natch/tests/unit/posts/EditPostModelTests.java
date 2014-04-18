package org.denevell.natch.tests.unit.posts;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.io.users.User;
import org.denevell.natch.model.entities.PostEntity;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("unused")
@Ignore // During refactor
public class EditPostModelTests {
	
	private EntityTransaction trans;
	private EntityManagerFactory factory;
	private EntityManager entityManager;

	@Before
	public void setup() {
		entityManager = mock(EntityManager.class);
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		when(entityManager.getTransaction()).thenReturn(trans);
	}
	
	@Ignore
	@Test
	public void shouldEditPost() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity(null, 1, 1, "df", "sdf", null);
		post.setUsername("this_person");
		//doReturn(post).when(model).findPostById(num);
		User User = new User();
		User.setUsername("this_person");
		//PostEntity postToBeEdited = new PostEntity(null, 1, 1, "xxx", "xxx", null);
//		when(postEntityAdapter.createPost(post, User)).thenReturn(postToBeEdited);
		
		// Act
//		String result = model.edit(User, num, postEntityAdapter, false);
		
		// Assert 
//		assertEquals(PostsModel.EDITED, result);
	}
	
	@Ignore
	@Test
	public void shouldReturnUnAuthorised() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUsername("this_person");
		//doReturn(post).when(model).findPostById(num);
		User User = new User();
		User.setUsername("that_person");
//		when(postEntityAdapter.createPost(post, User)).thenReturn(post);
		
		// Act
//		String result = model.edit(User, num, postEntityAdapter, false);
		
		// Assert 
//		assertEquals(PostsModel.NOT_YOURS_TO_DELETE, result);
	}
	
	@Ignore
	@Test
	public void shouldReturnNotFound() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUsername("this_person");
		//doReturn(post).when(model).findPostById(num+1);
		User User = new User();
		User.setUsername("that_person");
//		when(postEntityAdapter.createPost(post, User)).thenReturn(post);
		
		// Act
//		String result = model.edit(User, num, postEntityAdapter, false);
		
		// Assert 
//		assertEquals(PostsModel.DOESNT_EXIST, result);
	}
	
	@Ignore
	@Test
	public void shouldReturnUnknownErrorOnException() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUsername("this_person");
		//doThrow(new RuntimeException()).when(model).findPostById(num);
		User User = new User();
		User.setUsername("this_person");
//		when(postEntityAdapter.createPost(post, User)).thenReturn(post);
		
		// Act
//		String result = model.edit(User, num, postEntityAdapter, false);
		
		// Assert 
//		assertEquals(PostsModel.UNKNOWN_ERROR, result);
	}
	
	@Ignore
	@Test
	public void shouldReturnUnknownErorrOnNullUser() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUsername("this_person");
		//doReturn(post).when(model).findPostById(num);
		User User = new User();
		User.setUsername("this_person");
//		when(postEntityAdapter.createPost(post, User)).thenReturn(post);
		
		// Act
//		String result = model.edit(null, num, postEntityAdapter, false);
		
		// Assert 
//		assertEquals(PostsModel.UNKNOWN_ERROR, result);
	}
	
	@Ignore
	@Test
	public void shouldReturnUnknownErorrOnNullPost() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUsername("this_person");
		//doReturn(post).when(model).findPostById(num);
		User User = new User();
		User.setUsername("this_person");
		
		// Act
//		String result = model.edit(User, num, null, false);
		
		// Assert 
//		assertEquals(PostsModel.UNKNOWN_ERROR, result);
	}
	
	@Ignore
	@Test
	public void shouldReturnBadUserInputOnBlanks() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUsername("this_person");
		//doReturn(post).when(model).findPostById(num);
		User User = new User();
		User.setUsername("this_person");
		//PostEntity postToEdit = new PostEntity(null, 1, 1, " ", " ", null);
//		when(postEntityAdapter.createPost(post, User)).thenReturn(postToEdit);
		
		// Act
//		String result = model.edit(User, num, postEntityAdapter, false);
		
		// Assert 
//		assertEquals(PostsModel.BAD_USER_INPUT, result);
	}
	
	@Ignore
	@Test
	public void shouldReturnBadUserInputOnNull() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUsername("this_person");
		//doReturn(post).when(model).findPostById(num);
		User User = new User();
		User.setUsername("this_person");
		//PostEntity postToEdit = new PostEntity(null, 1, 1, null, null, null);
//		when(postEntityAdapter.createPost(post, User)).thenReturn(postToEdit);
		
		// Act
//		String result = model.edit(User, num, postEntityAdapter, true);
		
		// Assert 
//		assertEquals(PostsModel.BAD_USER_INPUT, result);
	}
	
	@Ignore
	@Test
	public void shouldReturnBadUserInputOnBlankSubject() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUsername("this_person");
		//doReturn(post).when(model).findPostById(num);
		User User = new User();
		User.setUsername("this_person");
		//PostEntity postToEdit = new PostEntity(null, 1, 1, " ", "sdfsdf", null);
//		when(postEntityAdapter.createPost(post, User)).thenReturn(postToEdit);
		
		// Act
//		String result = model.edit(User, num, postEntityAdapter, true);
		
		// Assert 
//		assertEquals(PostsModel.BAD_USER_INPUT, result);
	}
	
	@Ignore
	@Test
	public void shouldReturnUnknownErorrOnNullFromPostAdpater() {
		// Arrange
		long num = 1;
		PostEntity post = new PostEntity();
		post.setUsername("this_person");
		//doReturn(post).when(model).findPostById(num);
		User User = new User();
		User.setUsername("this_person");
//		when(postEntityAdapter.createPost(post, User)).thenReturn(null);
		
		// Act
		//String result = model.edit(User, num, postEntityAdapter, false);
		
		// Assert 
//		assertEquals(PostsModel.UNKNOWN_ERROR, result);
	}
}
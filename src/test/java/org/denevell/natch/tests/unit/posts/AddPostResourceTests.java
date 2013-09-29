package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.serv.posts.AddPostResourcePostEntityAdapter;
import org.denevell.natch.serv.posts.EditPostResourcePostEntityAdapter;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsREST;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class AddPostResourceTests {
	
	private PostsModel postsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private PostsREST resource;
	private UserEntity user;
	private HttpServletRequest request;
	private AddPostResourcePostEntityAdapter addPostAdapter;

	@Before
	public void setup() {
		postsModel = mock(PostsModel.class);
		user = new UserEntity();
		user.setUsername("dsf");
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		EditPostResourcePostEntityAdapter editPostAdapter = mock(EditPostResourcePostEntityAdapter.class);
		addPostAdapter = mock(AddPostResourcePostEntityAdapter.class);
		resource = new PostsREST(postsModel, request, response, editPostAdapter, addPostAdapter);
	}
	
	@Test
	public void shouldAddPost() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		when(addPostAdapter.getCreatedPost()).thenReturn(new PostEntity(null, 123, 123, "a", "dsf", "thready"));
		when(postsModel.addPost(user, addPostAdapter)).thenReturn(PostsModel.ADDED);
		
		// Act
		AddPostResourceReturnData result = resource.addPost_(input);
		
		// Assert
		assertTrue("Result is a success", result.isSuccessful());
		assertEquals("Error json", "", result.getError());
		assertEquals("ThreadId", "thready", result.getThreadId());
	}
	
	@Test
	public void shouldntAddThreadWithBlankSubject() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput(" ", "cont");
		when(addPostAdapter.getCreatedPost()).thenReturn(new PostEntity(null, 123, 123, "a", "dsf", "thready"));
		when(postsModel.addPost(user, addPostAdapter)).thenReturn(PostsModel.ADDED);
		
		// Act
		AddPostResourceReturnData result = resource.addThread(input);
		
		// Assert
		assertFalse("Result is a success", result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
	}	
	
	@Test
	public void shouldntAddThreadWithBlankContent() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput("sub", " ");
		when(addPostAdapter.getCreatedPost()).thenReturn(new PostEntity(null, 123, 123, "a", "dsf", "thready"));
		when(postsModel.addPost(user, addPostAdapter)).thenReturn(PostsModel.ADDED);
		
		// Act
		AddPostResourceReturnData result = resource.addThread(input);
		
		// Assert
		assertFalse("Result is a success", result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
	}		
	
	@Test
	public void shouldntAddPostWithBlankContent() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput("asdf", " ");
		when(addPostAdapter.getCreatedPost()).thenReturn(new PostEntity(null, 123, 123, "a", "dsf", "thready"));
		when(postsModel.addPost(user, addPostAdapter)).thenReturn(PostsModel.ADDED);
		
		// Act
		AddPostResourceReturnData result = resource.addPost_(input);
		
		// Assert
		assertFalse("Result is a success", result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
	}		
	
	@Test
	public void shouldntRegisterWhenModelSaysBadInput() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		when(postsModel.addPost(user, addPostAdapter)).thenReturn(PostsModel.BAD_USER_INPUT);
		
		// Act
		AddPostResourceReturnData result = resource.addPost_(input);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
	}
	
}
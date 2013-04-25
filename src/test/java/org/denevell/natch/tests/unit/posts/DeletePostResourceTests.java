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
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.AddPostResourcePostEntityAdapter;
import org.denevell.natch.serv.posts.EditPostResourcePostEntityAdapter;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsREST;
import org.denevell.natch.serv.posts.resources.DeletePostResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class DeletePostResourceTests {
	
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
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		EditPostResourcePostEntityAdapter postAdapter = mock(EditPostResourcePostEntityAdapter.class);
		addPostAdapter = mock(AddPostResourcePostEntityAdapter.class);
		resource = new PostsREST(postsModel, request, response, postAdapter, addPostAdapter);
	}
	
	@Test
	public void shouldDeletePost() {
		// Arrange
		long postEntityId = 1l;
		when(postsModel.delete(user, postEntityId)).thenReturn(PostsModel.DELETED);
		
		// Act
		DeletePostResourceReturnData result = resource.delete(postEntityId);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldShowNotYoursError() {
		// Arrange
		long postEntityId = 1l;
		when(postsModel.delete(user, postEntityId)).thenReturn(PostsModel.NOT_YOURS_TO_DELETE);
		
		// Act
		DeletePostResourceReturnData result = resource.delete(postEntityId);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_not_yours), result.getError());
	}
	
	@Test
	public void shouldShowUnknownPostError() {
		// Arrange
		long postEntityId = 1l;
		when(postsModel.delete(user, postEntityId)).thenReturn(PostsModel.DOESNT_EXIST);
		
		// Act
		DeletePostResourceReturnData result = resource.delete(postEntityId);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_doesnt_exist), result.getError());
	}
	
	@Test
	public void shouldShowUnknownErrorOnException() {
		// Arrange
		long postEntityId = 1l;
		when(postsModel.delete(user, postEntityId)).thenThrow(new RuntimeException());
		
		// Act
		DeletePostResourceReturnData result = resource.delete(postEntityId);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
	}
		
}
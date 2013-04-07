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
import org.denevell.natch.serv.posts.DeletePostResource;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsModel.DeletePostResult;
import org.denevell.natch.serv.posts.PostsResource;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class DeletePostResourceTests {
	
	private PostsModel postsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private PostsResource resource;
	private UserEntity user;
	private HttpServletRequest request;

	@Before
	public void setup() {
		postsModel = mock(PostsModel.class);
		user = new UserEntity();
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		resource = new PostsResource(postsModel, request, response);
	}
	
	@Test
	public void shouldDeletePost() {
		// Arrange
		long postEntityId = 1l;
		when(postsModel.delete(user, postEntityId)).thenReturn(DeletePostResult.DELETED);
		
		// Act
		DeletePostResource result = resource.delete(postEntityId);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldShowNotYoursError() {
		// Arrange
		long postEntityId = 1l;
		when(postsModel.delete(user, postEntityId)).thenReturn(DeletePostResult.NOT_YOURS_TO_DELETE);
		
		// Act
		DeletePostResource result = resource.delete(postEntityId);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_not_yours_to_delete), result.getError());
	}
	
	@Test
	public void shouldShowUnknownPostError() {
		// Arrange
		long postEntityId = 1l;
		when(postsModel.delete(user, postEntityId)).thenReturn(DeletePostResult.DOESNT_EXIST);
		
		// Act
		DeletePostResource result = resource.delete(postEntityId);
		
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
		DeletePostResource result = resource.delete(postEntityId);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
	}
		
}
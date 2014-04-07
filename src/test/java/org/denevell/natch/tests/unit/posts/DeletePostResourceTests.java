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
import org.denevell.natch.io.posts.DeletePostResourceReturnData;
import org.denevell.natch.serv.post.delete.DeletePostModel;
import org.denevell.natch.serv.post.delete.DeletePostRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class DeletePostResourceTests {
	
	private DeletePostModel postsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private DeletePostRequest resource;
	private UserEntity user;
	private HttpServletRequest request;

	@Before
	public void setup() {
		postsModel = mock(DeletePostModel.class);
		user = new UserEntity();
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		resource = new DeletePostRequest(postsModel, request, response);
	}
	
	@Test
	public void shouldDeletePost() {
		// Arrange
		long postEntityId = 1l;
		when(postsModel.delete(user, postEntityId)).thenReturn(DeletePostModel.DELETED);
		
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
		when(postsModel.delete(user, postEntityId)).thenReturn(DeletePostModel.NOT_YOURS_TO_DELETE);
		
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
		when(postsModel.delete(user, postEntityId)).thenReturn(DeletePostModel.DOESNT_EXIST);
		
		// Act
		DeletePostResourceReturnData result = resource.delete(postEntityId);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_doesnt_exist), result.getError());
	}
	
}
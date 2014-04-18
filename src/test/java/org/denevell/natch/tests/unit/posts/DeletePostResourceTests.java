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
import org.denevell.natch.io.posts.DeletePostResourceReturnData;
import org.denevell.natch.io.users.User;
import org.denevell.natch.serv.post.DeletePostRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

@Ignore // During refactor
public class DeletePostResourceTests {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private DeletePostRequest resource;
	private User user;
	private HttpServletRequest request;

	@Before
	public void setup() {
		user = new User();
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		resource = Mockito.spy(new DeletePostRequest(request, response));
	}
	
	@Test
	public void shouldDeletePost() {
		// Arrange
		long postEntityId = 1l;
		//when(resource.delete(user, postEntityId)).thenReturn(PostDeleteModel.DELETED);
		
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
		//when(resource.delete(user, postEntityId)).thenReturn(PostDeleteModel.NOT_YOURS);
		
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
		//when(resource.delete(user, postEntityId)).thenReturn(PostDeleteModel.DOESNT_EXIST);
		
		// Act
		DeletePostResourceReturnData result = resource.delete(postEntityId);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_doesnt_exist), result.getError());
	}
	
}
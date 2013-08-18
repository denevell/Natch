package org.denevell.natch.tests.unit.threads;

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
import org.denevell.natch.io.threads.DeleteThreadResourceReturnData;
import org.denevell.natch.serv.posts.PostModel;
import org.denevell.natch.serv.threads.ThreadModel;
import org.denevell.natch.serv.threads.ThreadsREST;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class DeleteThreadResourceTests {
	
	private ThreadModel threadsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private ThreadsREST resource;
	private UserEntity user;
	private HttpServletRequest request;

	@Before
	public void setup() {
		threadsModel = mock(ThreadModel.class);
		user = new UserEntity();
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		resource = new ThreadsREST(threadsModel, request, response);
	}
	
	@Test
	public void shouldDeletePost() {
		// Arrange
		long postEntityId = 1l;
		when(threadsModel.delete(user, postEntityId)).thenReturn(PostModel.DELETED);
		
		// Act
		DeleteThreadResourceReturnData result = resource.delete(postEntityId);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldShowNotYoursError() {
		// Arrange
		long postEntityId = 1l;
		when(threadsModel.delete(user, postEntityId)).thenReturn(PostModel.NOT_YOURS_TO_DELETE);
		
		// Act
		DeleteThreadResourceReturnData result = resource.delete(postEntityId);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_not_yours), result.getError());
	}
	
	@Test
	public void shouldShowDoesntExistPostError() {
		// Arrange
		long postEntityId = 1l;
		when(threadsModel.delete(user, postEntityId)).thenReturn(PostModel.DOESNT_EXIST);
		
		// Act
		DeleteThreadResourceReturnData result = resource.delete(postEntityId);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_doesnt_exist), result.getError());
	}
	
}
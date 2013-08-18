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
import org.denevell.natch.io.threads.EditThreadResourceInput;
import org.denevell.natch.io.threads.EditThreadResourceReturnData;
import org.denevell.natch.serv.threads.ThreadModel;
import org.denevell.natch.serv.threads.ThreadsREST;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class EditThreadResourceTests {
	
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
	public void shouldEditPost() {
		// Arrange
		EditThreadResourceInput editPostResource = new EditThreadResourceInput();
		when(threadsModel.edit(user, 1l, null, null, null)).thenReturn(ThreadModel.EDITED);
		
		// Act
		EditThreadResourceReturnData result = resource.edit(1l, editPostResource);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldShowNotYoursError() {
		// Arrange
		EditThreadResourceInput editPostResource = new EditThreadResourceInput();
		when(threadsModel.edit(user, 1l, null, null, null)).thenReturn(ThreadModel.NOT_YOURS_TO_DELETE);
		
		// Act
		EditThreadResourceReturnData result = resource.edit(1l, editPostResource);		
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_not_yours), result.getError());
	}
	
	@Test
	public void shouldShowUnknownPostError() {
		// Arrange
		EditThreadResourceInput editPostResource = new EditThreadResourceInput();
		when(threadsModel.edit(user, 1l, null, null, null)).thenReturn(ThreadModel.DOESNT_EXIST);
		
		// Act
		EditThreadResourceReturnData result = resource.edit(1l, editPostResource);		
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_doesnt_exist), result.getError());
	}
}
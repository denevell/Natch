package org.denevell.natch.tests.unit.threads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.threads.AddThreadResourceInput;
import org.denevell.natch.io.threads.AddThreadResourceReturnData;
import org.denevell.natch.serv.threads.ThreadModel;
import org.denevell.natch.serv.threads.ThreadsREST;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class AddThreadResourceTests {
	
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
	public void shouldAddThread() {
		// Arrange
		AddThreadResourceInput input = new AddThreadResourceInput("s", "c");
		when(threadsModel.addThread(user, "s", "c", new ArrayList<String>())).thenReturn("123");
		
		// Act
		AddThreadResourceReturnData result = resource.add(input);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
		assertEquals("ThreadId", 123, result.getThreadId());
	}
	
//	@Test
//	public void shouldntRegisterWhenModelSaysBadInput() {
//		// Arrange
//		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
//		when(postsModel.addPost(user, addPostAdapter)).thenReturn(PostsModel.BAD_USER_INPUT);
//		
//		// Act
//		AddPostResourceReturnData result = resource.add(input);
//		
//		// Assert
//		assertFalse(result.isSuccessful());
//		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
//	}
//	
//	@Test
//	public void shouldntAddWhenDodgyUserObjectInRequest() {
//		// Arrange
//		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
//		when(postsModel.addPost(user, addPostAdapter)).thenReturn(PostsModel.ADDED);
//		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenThrow(new RuntimeException());
//		
//		// Act
//		AddPostResourceReturnData result = resource.add(input);
//		
//		// Assert
//		assertFalse(result.isSuccessful());
//		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
//	}
//	
//	@Test
//	public void shouldntWithNullInputObject() {
//		// Arrange
//		
//		// Act
//		AddPostResourceReturnData result = resource.add(null);
//		
//		// Assert
//		assertFalse(result.isSuccessful());
//		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
//	}
//	
//	@Test
//	public void shouldntAddWithUnknownError() {
//		// Arrange
//		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
//		when(postsModel.addPost(user, addPostAdapter)).thenReturn(PostsModel.UNKNOWN_ERROR);
//		
//		// Act
//		AddPostResourceReturnData result = resource.add(input);
//		
//		// Assert
//		assertFalse(result.isSuccessful());
//		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
//	}
	
}
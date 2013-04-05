package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.AddPostResourceInput;
import org.denevell.natch.serv.posts.AddPostResourceReturnData;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsModel.AddPostResult;
import org.denevell.natch.serv.posts.PostsResource;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class AddPostResourceTests {
	
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
		resource = new PostsResource(postsModel, request);
	}
	
	@Test
	public void shouldAddPost() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		when(postsModel.addPost(user, "sub", "cont")).thenReturn(AddPostResult.ADDED);
		
		// Act
		AddPostResourceReturnData result = resource.add(input);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldntRegisterWhenModelSaysBadInput() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		when(postsModel.addPost(user, "sub", "cont")).thenReturn(AddPostResult.BAD_USER_INPUT);
		
		// Act
		AddPostResourceReturnData result = resource.add(input);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
	}
	
	@Test
	public void shouldntAddWhenDodgyUserObjectInRequest() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		when(postsModel.addPost(user, "sub", "cont")).thenReturn(AddPostResult.ADDED);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenThrow(new RuntimeException());
		
		// Act
		AddPostResourceReturnData result = resource.add(input);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
	}
	
	@Test
	public void shouldntRegisterWithNullInputObject() {
		// Arrange
		
		// Act
		AddPostResourceReturnData result = resource.add(null);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
	}
	
	@Test
	public void shouldntRegisterWithUnknownError() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		when(postsModel.addPost(user, "sub", "cont")).thenReturn(AddPostResult.UNKNOWN_ERROR);
		
		// Act
		AddPostResourceReturnData result = resource.add(input);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
	}
	
}
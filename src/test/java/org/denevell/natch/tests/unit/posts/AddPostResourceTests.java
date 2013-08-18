package org.denevell.natch.tests.unit.posts;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostModel;
import org.denevell.natch.serv.posts.PostsREST;
import org.denevell.natch.utils.Strings;
import org.junit.Before;

public class AddPostResourceTests {
	
	private PostModel postsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private PostsREST resource;
	private UserEntity user;
	private HttpServletRequest request;

	@Before
	public void setup() {
		postsModel = mock(PostModel.class);
		user = new UserEntity();
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		resource = new PostsREST(postsModel, request, response);
	}
//	
//	@Test
//	public void shouldAddPost() {
//		// Arrange
//		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
//		when(addPostAdapter.getCreatedPost()).thenReturn(new PostEntity(null, 123, 123, "a", "dsf", "thready"));
//		when(postsModel.addPost(user, addPostAdapter)).thenReturn(PostsModel.ADDED);
//		
//		// Act
//		AddPostResourceReturnData result = resource.add(input);
//		
//		// Assert
//		assertTrue(result.isSuccessful());
//		assertEquals("Error json", "", result.getError());
//		assertEquals("ThreadId", "thready", result.getThreadId());
//	}
//	
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
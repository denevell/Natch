package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.users.User;
import org.denevell.natch.serv.thread.AddThreadRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Ignoring during refactor
 *
 */
@Ignore
public class AddThreadResourceTests {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private AddThreadRequest resource;
	private User user;
	private HttpServletRequest request;

	@Before
	public void setup() {
		user = new User();
		user.setUsername("dsf");
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		//resource = new AddThreadRequest(postsModel, request, response);
	}

	@Test
	public void shouldntAddThreadWithBlankSubject() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput(" ", "cont");
		//when(postsModel.addPost(user, addPostAdapter)).thenReturn(mock(ThreadEntity.class));
		
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
		//when(postsModel.addPost(user, addPostAdapter)).thenReturn(mock(ThreadEntity.class));
		
		// Act
		AddPostResourceReturnData result = resource.addThread(input);
		
		// Assert
		assertFalse("Result is a success", result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
	}		
	
}
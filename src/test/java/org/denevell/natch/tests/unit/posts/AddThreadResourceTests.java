package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.serv.add_thread.AddThreadRequest;
import org.denevell.natch.serv.posts.AddPostResourcePostEntityAdapter;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class AddThreadResourceTests {
	
	private PostsModel postsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private AddThreadRequest resource;
	private UserEntity user;
	private HttpServletRequest request;
	private AddPostResourcePostEntityAdapter addPostAdapter;

	@Before
	public void setup() {
		postsModel = mock(PostsModel.class);
		user = new UserEntity();
		user.setUsername("dsf");
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		addPostAdapter = mock(AddPostResourcePostEntityAdapter.class);
		resource = new AddThreadRequest(postsModel, request, response, addPostAdapter);
	}

	@Test
	public void shouldntAddThreadWithBlankSubject() {
		// Arrange
		AddPostResourceInput input = new AddPostResourceInput(" ", "cont");
		when(addPostAdapter.getCreatedPost()).thenReturn(new PostEntity(null, 123, 123, "a", "dsf", "thready"));
		when(postsModel.addPost(user, addPostAdapter)).thenReturn(mock(ThreadEntity.class));
		
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
		when(addPostAdapter.getCreatedPost()).thenReturn(new PostEntity(null, 123, 123, "a", "dsf", "thready"));
		when(postsModel.addPost(user, addPostAdapter)).thenReturn(mock(ThreadEntity.class));
		
		// Act
		AddPostResourceReturnData result = resource.addThread(input);
		
		// Assert
		assertFalse("Result is a success", result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
	}		
	
}
package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsModel.EditPostResult;
import org.denevell.natch.serv.posts.PostsREST;
import org.denevell.natch.serv.posts.resources.EditPostResource;
import org.denevell.natch.serv.posts.resources.EditPostResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class EditPostResourceTests {
	
	private PostsModel postsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private PostsREST resource;
	private UserEntity user;
	private HttpServletRequest request;

	@Before
	public void setup() {
		postsModel = mock(PostsModel.class);
		user = new UserEntity();
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		resource = new PostsREST(postsModel, request, response);
	}
	
	@Test
	public void shouldEditPost() {
		// Arrange
		EditPostResource editPostResource = null;
		long postEntityId = 1l;
		PostEntity postEntity = new PostEntity();
		when(postsModel.edit(user, postEntityId, postEntity)).thenReturn(EditPostResult.EDITED);
		
		// Act
		EditPostResourceReturnData result = resource.edit(editPostResource);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldShowNotYoursError() {
	}
	
	@Test
	public void shouldShowUnknownPostError() {
	}
	
	@Test
	public void shouldShowUnknownErrorOnException() {
	}
		
}
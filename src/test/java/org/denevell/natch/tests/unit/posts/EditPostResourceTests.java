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
import org.denevell.natch.serv.posts.PostEntityAdapter;
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
	private PostEntityAdapter postEntityAdapter;

	@Before
	public void setup() {
		postsModel = mock(PostsModel.class);
		user = new UserEntity();
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		HttpServletResponse response = mock(HttpServletResponse.class);
		postEntityAdapter = mock(PostEntityAdapter.class);
		resource = new PostsREST(postsModel, request, response, postEntityAdapter);
	}
	
	@Test
	public void shouldEditPost() {
		// Arrange
		EditPostResource editPostResource = null;
		long postEntityId = 1l;
		when(postsModel.edit(user, postEntityId, postEntityAdapter)).thenReturn(EditPostResult.EDITED);
		
		// Act
		EditPostResourceReturnData result = resource.edit(postEntityId, editPostResource);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldShowNotYoursError() {
		// Arrange
		EditPostResource editPostResource = null;
		long postEntityId = 1l;
		when(postsModel.edit(user, postEntityId, postEntityAdapter)).thenReturn(EditPostResult.NOT_YOURS_TO_DELETE);
		
		// Act
		EditPostResourceReturnData result = resource.edit(postEntityId, editPostResource);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_not_yours), result.getError());
	}
	
	@Test
	public void shouldShowUnknownPostError() {
		// Arrange
		EditPostResource editPostResource = null;
		long postEntityId = 1l;
		when(postsModel.edit(user, postEntityId, postEntityAdapter)).thenReturn(EditPostResult.DOESNT_EXIST);
		
		// Act
		EditPostResourceReturnData result = resource.edit(postEntityId, editPostResource);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_doesnt_exist), result.getError());
	}
	
	@Test
	public void shouldShowUnknownErrorOnException() {
		// Arrange
		EditPostResource editPostResource = null;
		long postEntityId = 1l;
		when(postsModel.edit(user, postEntityId, postEntityAdapter)).thenThrow(new RuntimeException());
		
		// Act
		EditPostResourceReturnData result = resource.edit(postEntityId, editPostResource);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.unknown_error), result.getError());
	}
		
}
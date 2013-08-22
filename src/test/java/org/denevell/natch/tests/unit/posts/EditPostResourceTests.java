package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.serv.posts.PostModel;
import org.denevell.natch.serv.posts.PostsREST;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class EditPostResourceTests {
	
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
	
	@Test
	public void shouldEditPost() {
		// Arrange
		EditPostResource editPostResource = new EditPostResource();
		editPostResource.setContent("asdf");
		long postEntityId = 1l;
		when(postsModel.edit(user, postEntityId, "asdf")).thenReturn(PostModel.EDITED);
		
		// Act
		EditPostResourceReturnData result = resource.edit(postEntityId, editPostResource);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Test
	public void shouldShowNotYoursError() {
		// Arrange
		EditPostResource editPostResource = new EditPostResource();
		editPostResource.setContent("asdf");
		long postEntityId = 1l;
		when(postsModel.edit(user, postEntityId, "asdf")).thenReturn(PostModel.NOT_YOURS_TO_DELETE);
		
		// Act
		EditPostResourceReturnData result = resource.edit(postEntityId, editPostResource);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_not_yours), result.getError());
	}
	
	@Test
	public void shouldShowBadUserInputPostError() {
		// Arrange
		EditPostResource editPostResource = new EditPostResource();
		editPostResource.setContent("asdf");
		long postEntityId = 1l;
		when(postsModel.edit(user, postEntityId, "asdf")).thenReturn(PostModel.BAD_USER_INPUT);
		
		// Act
		EditPostResourceReturnData result = resource.edit(postEntityId, editPostResource);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_fields_cannot_be_blank), result.getError());
	}
	
		
}
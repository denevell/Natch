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
import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.serv.post.edit.EditPostRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EditPostResourceTests {
	
	//private PostsModel postsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private EditPostRequest resource;
	private UserEntity user;
	private HttpServletRequest request;

	@Before
	public void setup() {
		//postsModel = mock(PostsModel.class);
		user = new UserEntity();
		request = mock(HttpServletRequest.class);
		when(request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER)).thenReturn(user);
		//HttpServletResponse response = mock(HttpServletResponse.class);
		//resource = new EditPostRequest(postsModel, request, response, postEntityAdapter);
	}
	
	@Ignore
	@Test
	public void shouldEditPost() {
		// Arrange
		EditPostResource editPostResource = null;
		long postEntityId = 1l;
		//when(postsModel.edit(user, postEntityId, postEntityAdapter, false)).thenReturn(PostsModel.EDITED);
		
		// Act
		EditPostResourceReturnData result = resource.editpost(postEntityId, editPostResource);
		
		// Assert
		assertTrue(result.isSuccessful());
		assertEquals("Error json", "", result.getError());
	}
	
	@Ignore
	@Test
	public void shouldShowNotYoursError() {
		// Arrange
		EditPostResource editPostResource = null;
		long postEntityId = 1l;
		//when(postsModel.edit(user, postEntityId, postEntityAdapter, false)).thenReturn(PostsModel.NOT_YOURS_TO_DELETE);
		
		// Act
		EditPostResourceReturnData result = resource.editpost(postEntityId, editPostResource);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_not_yours), result.getError());
	}
	
	@Ignore
	@Test
	public void shouldShowUnknownPostError() {
		// Arrange
		EditPostResource editPostResource = null;
		long postEntityId = 1l;
		//when(postsModel.edit(user, postEntityId, postEntityAdapter, false)).thenReturn(PostsModel.DOESNT_EXIST);
		
		// Act
		EditPostResourceReturnData result = resource.editpost(postEntityId, editPostResource);
		
		// Assert
		assertFalse(result.isSuccessful());
		assertEquals("Error json", rb.getString(Strings.post_doesnt_exist), result.getError());
	}
	
}
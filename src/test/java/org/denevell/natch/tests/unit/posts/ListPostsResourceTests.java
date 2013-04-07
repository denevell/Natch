package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.ListPostsResource;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsResource;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class ListPostsResourceTests {
	
	private PostsModel postsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private PostsResource resource;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Before
	public void setup() {
		postsModel = mock(PostsModel.class);
		response = mock(HttpServletResponse.class);
		resource = new PostsResource(postsModel, request, response);
	}
	
	@Test
	public void shouldListPosts() throws IOException {
		// Arrange
		List<PostEntity> posts = new ArrayList<PostEntity>();
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", null);
		postEntity.setId(400);
		posts.add(postEntity);
		posts.add(new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", null));
		when(postsModel.listByModificationDate()).thenReturn(posts);
		
		// Act
		ListPostsResource result = resource.listByModificationDate();
		
		// Assert
		assertEquals(2, result.getPosts().size());
		assertEquals(400, result.getPosts().get(0).getId());
		assertEquals(1, result.getPosts().get(0).getCreation());
		assertEquals(1, result.getPosts().get(0).getModification());
		assertEquals("u1", result.getPosts().get(0).getUsername());
		assertEquals("s1", result.getPosts().get(0).getSubject());
		assertEquals("c1", result.getPosts().get(0).getContent());
		assertEquals(2, result.getPosts().get(1).getCreation());
		assertEquals(2, result.getPosts().get(1).getModification());
		assertEquals("u2", result.getPosts().get(1).getUsername());
		assertEquals("s2", result.getPosts().get(1).getSubject());
		assertEquals("c2", result.getPosts().get(1).getContent());
	}
	
	@Test
	public void shouldListZeroPosts() throws IOException {
		// Arrange
		List<PostEntity> posts = new ArrayList<PostEntity>();
		when(postsModel.listByModificationDate()).thenReturn(posts);
		
		// Act
		ListPostsResource result = resource.listByModificationDate();
		
		// Assert
		assertEquals(0, result.getPosts().size());
	}
	
	@Test
	public void shouldThrow500OnNullFromModel() throws IOException {
		// Arrange
		when(postsModel.listByModificationDate()).thenReturn(null);
		
		// Act
		resource.listByModificationDate();
		
		// Assert
		verify(response).sendError(500, "Unexcepted error");
	}
	
	@Test
	public void shouldThrow500OnExceptionFromModel() throws IOException {
		// Arrange
		when(postsModel.listByModificationDate()).thenThrow(new RuntimeException());
		
		// Act
		resource.listByModificationDate();
		
		// Assert
		verify(response).sendError(500, "Unexcepted error");
	}
}
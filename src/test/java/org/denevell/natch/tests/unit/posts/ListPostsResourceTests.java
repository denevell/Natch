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
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.serv.posts.AddPostResourcePostEntityAdapter;
import org.denevell.natch.serv.posts.EditPostResourcePostEntityAdapter;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsREST;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

public class ListPostsResourceTests {
	
	private PostsModel postsModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private PostsREST resource;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AddPostResourcePostEntityAdapter addPostAdapter;

	@Before
	public void setup() {
		postsModel = mock(PostsModel.class);
		response = mock(HttpServletResponse.class);
		request = mock(HttpServletRequest.class);
		EditPostResourcePostEntityAdapter postAdapter = mock(EditPostResourcePostEntityAdapter.class);
		addPostAdapter = mock(AddPostResourcePostEntityAdapter.class);
		resource = new PostsREST(postsModel, request, response, postAdapter, addPostAdapter);
	}
	
	@Test
	public void shouldFindSinglePost() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", null);
		@SuppressWarnings("unchecked") List<String> asList = Arrays.asList(new String[] {"tag1"});
		postEntity.setTags(asList);
		postEntity.setId(400);
		postEntity.setThreadId("1234");
		when(postsModel.findPostById(0)).thenReturn(postEntity);
		
		// Act
		PostResource result = resource.findById(0);
		
		// Assert
		assertEquals(1, result.getCreation());
		assertEquals(1, result.getModification());
		assertEquals("1234", result.getThreadId());
		assertEquals("u1", result.getUsername());
		assertEquals("s1", result.getSubject());
		assertEquals("c1", result.getContent());
		assertEquals("tag1", result.getTags().get(0));
		assertEquals(400, result.getId());
	}	
	
	@Test
	public void shouldListPosts() throws IOException {
		// Arrange
		List<PostEntity> posts = new ArrayList<PostEntity>();
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", null);
		@SuppressWarnings("unchecked") List<String> asList = Arrays.asList(new String[] {"tag1"});
		postEntity.setTags(asList);
		postEntity.setId(400);
		posts.add(postEntity);
		posts.add(new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", null));
		when(postsModel.listByModificationDate(0, 10)).thenReturn(posts);
		
		// Act
		ListPostsResource result = resource.listByModificationDate(0, 10);
		
		// Assert
		assertEquals(2, result.getPosts().size());
		assertEquals(400, result.getPosts().get(0).getId());
		assertEquals(1, result.getPosts().get(0).getCreation());
		assertEquals(1, result.getPosts().get(0).getModification());
		assertEquals("u1", result.getPosts().get(0).getUsername());
		assertEquals("s1", result.getPosts().get(0).getSubject());
		assertEquals("c1", result.getPosts().get(0).getContent());
		assertEquals("tag1", result.getPosts().get(0).getTags().get(0));
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
		when(postsModel.listByModificationDate(0, 10)).thenReturn(posts);
		
		// Act
		ListPostsResource result = resource.listByModificationDate(0, 10);
		
		// Assert
		assertEquals(0, result.getPosts().size());
	}
	
	@Test
	public void shouldThrow500OnNullFromModel() throws IOException {
		// Arrange
		when(postsModel.listByModificationDate(0, 10)).thenReturn(null);
		
		// Act
		resource.listByModificationDate(0, 10);
		
		// Assert
		verify(response).sendError(500, "Unexcepted error");
	}
	
	@Test
	public void shouldThrow500OnExceptionFromModel() throws IOException {
		// Arrange
		when(postsModel.listByModificationDate(0, 10)).thenThrow(new RuntimeException());
		
		// Act
		resource.listByModificationDate(0, 10);
		
		// Assert
		verify(response).sendError(500, "Unexcepted error");
	}
	
	@Test
	public void shouldListPostsWithThreadId() throws IOException {
		// Arrange
		List<PostEntity> posts = new ArrayList<PostEntity>();
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "threadId");
		postEntity.setId(400);
		posts.add(postEntity);
		when(postsModel.listByModificationDate(0, 10)).thenReturn(posts);
		
		// Act
		ListPostsResource result = resource.listByModificationDate(0, 10);
		
		// Assert
		assertEquals("threadId", result.getPosts().get(0).getThreadId());
	}	
	
	@Test
	public void shouldListPostsByThread() throws IOException {
		// Arrange
		List<PostEntity> posts = new ArrayList<PostEntity>();
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		postEntity.setId(400);
		posts.add(postEntity);
		posts.add(new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", "t"));
		when(postsModel.listByThreadId("t", 0, 10)).thenReturn(posts);
		
		// Act
		ListPostsResource result = resource.listByThreadId("t", 0, 10);
		
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldListThreads() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		postEntity.setId(400);
		PostEntity postEntity1 = new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", "t");
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		threads.add(new ThreadEntity(postEntity, Arrays.asList(new PostEntity[] { postEntity } )));
		threads.add(new ThreadEntity(postEntity1, Arrays.asList(new PostEntity[] { postEntity1 } )));
		when(postsModel.listThreads(0, 10)).thenReturn(threads);
		
		// Act
		ListPostsResource result = resource.listThreads(0, 10);
		
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldntListThreadsWhenWithAnInitialPostAsNull() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		PostEntity postEntity1 = new PostEntity(new UserEntity("u2", ""), 1, 1, "s2", "c2", "t");
		postEntity.setId(400);
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		threads.add(new ThreadEntity(null, Arrays.asList(new PostEntity[] { postEntity } )));
		threads.add(new ThreadEntity(postEntity1, Arrays.asList(new PostEntity[] { postEntity1 } )));
		when(postsModel.listThreads(0, 10)).thenReturn(threads);
		
		// Act
		ListPostsResource result = resource.listThreads(0, 10);
		
		// Assert
		assertEquals(1, result.getPosts().size());
	}		
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldListThreadsByTag() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "t");
		postEntity.setId(400);
		PostEntity postEntity1 = new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", "t");
		List<ThreadEntity> threads = new ArrayList<ThreadEntity>();
		threads.add(new ThreadEntity(postEntity, Arrays.asList(new PostEntity[] { postEntity } )));
		threads.add(new ThreadEntity(postEntity1, Arrays.asList(new PostEntity[] { postEntity1 } )));
		when(postsModel.listThreadsByTag("tagy", 0, 10)).thenReturn(threads);
		
		// Act
		ListPostsResource result = resource.listThreadsByTag("tagy", 0, 10);
		
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
	
}
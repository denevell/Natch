package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.serv.post.ListPosts;
import org.denevell.natch.serv.thread.ListThreadRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class ListPostsResourceTests {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ListPosts resourceList;
	private CallDbBuilder<PostEntity> postsModel;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		postsModel = mock(CallDbBuilder.class);
		when(postsModel.max(0)).thenReturn(postsModel);
		when(postsModel.start(0)).thenReturn(postsModel);
		when(postsModel.namedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE)).thenReturn(postsModel);
		response = mock(HttpServletResponse.class);
		request = mock(HttpServletRequest.class);
		resourceList = new ListPosts(postsModel, request, response);
	}
	
	@Test
	public void shouldListPosts() throws IOException {
		// Arrange
		List<PostEntity> posts = new ArrayList<PostEntity>();
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", null);
		List<String> asList = Arrays.asList(new String[] {"tag1"});
		postEntity.setTags(asList);
		postEntity.setId(400);
		posts.add(postEntity);
		posts.add(new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", null));
		when(postsModel.list(PostEntity.class)).thenReturn(posts);
		
		// Act
		ListPostsResource result = resourceList.listByModificationDate(0, 0);
		
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
		when(postsModel.list(PostEntity.class)).thenReturn(posts);
		
		// Act
		ListPostsResource result = resourceList.listByModificationDate(0, 0);
		
		// Assert
		assertEquals(0, result.getPosts().size());
	}
		
	@Test
	public void shouldListPostsWithThreadId() throws IOException {
		// Arrange
		List<PostEntity> posts = new ArrayList<PostEntity>();
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", "threadId");
		postEntity.setId(400);
		posts.add(postEntity);
		when(postsModel.list(PostEntity.class)).thenReturn(posts);
		
		// Act
		ListPostsResource result = resourceList.listByModificationDate(0, 0);
		
		// Assert
		assertEquals("threadId", result.getPosts().get(0).getThreadId());
	}	
	
	@Test
	public void shouldListPostsByThread() throws IOException {
		// Arrange
		List<PostEntity> posts = new ArrayList<PostEntity>();
		UserEntity ue = new UserEntity("u1", "");
		PostEntity postEntity = new PostEntity(ue, 1, 1, "s1", "c1", "t");
		postEntity.setId(400);
		posts.add(postEntity);
		posts.add(new PostEntity(new UserEntity("u2", ""), 2, 2, "s2", "c2", "t"));
		ThreadEntity thread = new ThreadEntity(postEntity, posts);
		thread.setNumPosts(5);
		
		@SuppressWarnings("unchecked")
		CallDbBuilder<ThreadEntity> threadModel = mock(CallDbBuilder.class);
		when(threadModel.max(0)).thenReturn(threadModel);
		when(threadModel.start(0)).thenReturn(threadModel);
		when(threadModel.queryParam("id", "t")).thenReturn(threadModel);
		when(threadModel.namedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID)).thenReturn(threadModel);
		when(threadModel.single(ThreadEntity.class)).thenReturn(thread);

		@SuppressWarnings("unchecked")
		CallDbBuilder<PostEntity> newPostsModel = mock(CallDbBuilder.class);
		when(newPostsModel.max(0)).thenReturn(newPostsModel);
		when(newPostsModel.start(0)).thenReturn(newPostsModel);
		when(newPostsModel.queryParam("threadId", "t")).thenReturn(newPostsModel);
		when(newPostsModel.namedQuery(PostEntity.NAMED_QUERY_FIND_BY_THREADID)).thenReturn(newPostsModel);
		when(newPostsModel.list(PostEntity.class)).thenReturn(posts);

		// Act
		ListThreadRequest res = new ListThreadRequest(newPostsModel, threadModel, request, response);
		ThreadResource result = res.listByThreadId("t", 0, 0);
		
		// Assert
		assertEquals(2, result.getPosts().size());
		assertEquals(5, result.getNumPosts());
		assertEquals(400, result.getPosts().get(0).getId());
		assertEquals(1, result.getPosts().get(0).getCreation());
		assertEquals(1, result.getPosts().get(0).getModification());
		assertEquals("s1", result.getSubject());
		assertEquals("u1", result.getAuthor());
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
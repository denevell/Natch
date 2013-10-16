package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.serv.post.show.ShowPostModel;
import org.denevell.natch.serv.post.show.SinglePostRequest;
import org.denevell.natch.serv.thread.list.ListThreadModel;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

public class ShowPostRequestTests {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ShowPostModel showPostsModel;
	private ListThreadModel listThreadsModel;

	@Before
	public void setup() {
		showPostsModel = mock(ShowPostModel.class);
		listThreadsModel = mock(ListThreadModel.class);
		response = mock(HttpServletResponse.class);
		request = mock(HttpServletRequest.class);
	}
	
	@Test
	public void shouldFindSinglePost() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", null);
		@SuppressWarnings("unchecked") List<String> asList = Arrays.asList(new String[] {"tag1"});
		postEntity.setTags(asList);
		postEntity.setId(400);
		postEntity.setThreadId("1234");
		when(showPostsModel.findPostById(0)).thenReturn(postEntity);
		SinglePostRequest resourceShow = new SinglePostRequest(showPostsModel, listThreadsModel, request, response);
		// The thread for the subject
		PostEntity threadRootPost = new PostEntity();
		threadRootPost.setSubject("thread_subject");
		ThreadEntity thread = new ThreadEntity(threadRootPost, null);
		when(listThreadsModel.findThreadById("1234")).thenReturn(thread);
		
		// Act
		PostResource result = resourceShow.findById(0);
		
		// Assert
		assertEquals(1, result.getCreation());
		assertEquals(1, result.getModification());
		assertEquals("1234", result.getThreadId());
		assertEquals("u1", result.getUsername());
		assertEquals("thread_subject", result.getSubject());
		assertEquals("c1", result.getContent());
		assertEquals("tag1", result.getTags().get(0));
		assertEquals(400, result.getId());
	}	
	
}
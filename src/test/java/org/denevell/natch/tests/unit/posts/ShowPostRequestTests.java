package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.serv.post.SinglePostRequest;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class ShowPostRequestTests {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private HttpServletRequest request;
	private HttpServletResponse response;
	@SuppressWarnings("rawtypes")
	private CallDbBuilder showPostsModel = mock(CallDbBuilder.class);
	@SuppressWarnings("rawtypes")
	private CallDbBuilder listThreadsModel = mock(CallDbBuilder.class);
	

	@Before
	public void setup() {
		response = mock(HttpServletResponse.class);
		request = mock(HttpServletRequest.class);

		when(showPostsModel.namedQuery(PostEntity.NAMED_QUERY_FIND_BY_ID)).thenReturn(showPostsModel);
		when(listThreadsModel.namedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID)).thenReturn(listThreadsModel);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldFindSinglePost() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity(new UserEntity("u1", ""), 1, 1, "s1", "c1", null);
		List<String> asList = Arrays.asList(new String[] {"tag1"});
		postEntity.setTags(asList);
		postEntity.setId(400);
		postEntity.setThreadId("1234");
		when(showPostsModel.startTransaction()).thenReturn(showPostsModel);
		when(showPostsModel.queryParam("id", 0l)).thenReturn(showPostsModel);
		when(showPostsModel.single(PostEntity.class)).thenReturn(postEntity);
		when(listThreadsModel.queryParam("id", "1234")).thenReturn(listThreadsModel);
		PostEntity threadRootPost = new PostEntity();
		threadRootPost.setSubject("thread_subject");
		ThreadEntity thread = new ThreadEntity(threadRootPost, null);
		when(listThreadsModel.useTransaction(showPostsModel.getEntityManager())).thenReturn(listThreadsModel);
		when(listThreadsModel.single(ThreadEntity.class)).thenReturn(thread);
		SinglePostRequest resourceShow = new SinglePostRequest(
				showPostsModel, 
				listThreadsModel, 
				request, 
				response);
		
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
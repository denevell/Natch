package org.denevell.natch.serv.post.show;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.utils.Log;

@Path("post/single")
public class SinglePostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private CallDbBuilder<PostEntity> mModel;
	private CallDbBuilder<ThreadEntity> mListThreadModel;
	
	public SinglePostRequest() {
		mModel = new CallDbBuilder<PostEntity>();

		mListThreadModel = new CallDbBuilder<ThreadEntity>();
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public SinglePostRequest(CallDbBuilder<PostEntity> postModel,
			CallDbBuilder<ThreadEntity> threadModel,
			HttpServletRequest request, HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
		mListThreadModel = threadModel;
	}

	@GET
	@Path("{postId}")
	@Produces(MediaType.APPLICATION_JSON)
	public PostResource findById(
		@PathParam("postId") long postId) throws IOException {
		PostEntity post = null;
		ThreadEntity thread = null;
		try {
			post = mModel
				.namedQuery(PostEntity.NAMED_QUERY_FIND_BY_ID)
				.queryParam("id", postId)
				.single(PostEntity.class);
			if(post==null) {
				mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			} else {
				thread = mListThreadModel
						.namedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID)
						.queryParam("id", post.getThreadId())
						.single(ThreadEntity.class);		
				PostResource postResource = new PostResource(post.getUser().getUsername(), 
						post.getCreated(), 
						post.getModified(), 
						thread.getRootPost().getSubject(), 
						post.getContent(), 
						post.getTags(), 
						post.isAdminEdited());
				postResource.setId(post.getId());
				postResource.setThreadId(post.getThreadId());
				return postResource;
			}
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't find post: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} 
	}	

}

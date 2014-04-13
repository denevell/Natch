package org.denevell.natch.serv.post;

import java.io.IOException;

import javax.inject.Inject;
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
import org.denevell.natch.model.interfaces.PostSingleModel;

@Path("post/single")
public class SinglePostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostSingleModel mPostSingle;
	
	public SinglePostRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public SinglePostRequest(CallDbBuilder<PostEntity> postModel,
			CallDbBuilder<ThreadEntity> threadModel,
			HttpServletRequest request, HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}

	@GET
	@Path("{postId}")
	@Produces(MediaType.APPLICATION_JSON)
	public PostResource findById(@PathParam("postId") long postId) throws IOException {
		PostEntity post = mPostSingle.find(postId);
		if(post==null) {
			mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		PostResource postResource = new PostResource(
				post.getUser().getUsername(), 
				post.getCreated(), 
				post.getModified(),
				post.getSubject(), 
				post.getContent(), 
				post.getTags(),
				post.isAdminEdited());
		postResource.setId(post.getId());
		postResource.setThreadId(post.getThreadId());
		return postResource;
	}

}

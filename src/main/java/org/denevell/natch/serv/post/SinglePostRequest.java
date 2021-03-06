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

import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.model.entities.PostEntity;
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
	public SinglePostRequest(PostSingleModel model, HttpServletRequest request, HttpServletResponse response) {
		mPostSingle = model;
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
				post.getUsername(), 
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

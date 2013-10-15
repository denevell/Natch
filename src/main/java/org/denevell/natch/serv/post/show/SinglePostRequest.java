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

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.utils.Log;

@Path("post/single")
public class SinglePostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private PostsModel mModel;
	
	public SinglePostRequest() {
		mModel = new PostsModel();
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public SinglePostRequest(PostsModel postModel, HttpServletRequest request, HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}

	@GET
	@Path("{postId}")
	@Produces(MediaType.APPLICATION_JSON)
	public PostResource findById(
		@PathParam("postId") long postId) throws IOException {
		PostEntity post = null;
		try {
			mModel.init();
			post = mModel.findPostById(postId);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't find post: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} finally {
			mModel.close();
		}
		if(post==null) {
			mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} else {
			PostResource postResource = new PostResource(post.getUser().getUsername(), 
					post.getCreated(), 
					post.getModified(), 
					post.getSubject(), 
					post.getContent(), 
					post.getTags());
			postResource.setId(post.getId());
			postResource.setThreadId(post.getThreadId());
			return postResource;
		}
	}	

}

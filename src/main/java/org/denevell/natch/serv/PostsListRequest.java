package org.denevell.natch.serv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import org.denevell.natch.model.PostEntity;
import org.denevell.natch.model.PostsListByModDateModel;
import org.denevell.natch.serv.PostSingleRequest.PostResource;

@Path("post")
public class PostsListRequest {
	
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostsListByModDateModel mPostsListModel;
	
	public PostsListRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public PostsListRequest(PostsListByModDateModel model, HttpServletRequest request, HttpServletResponse response) {
		mPostsListModel = model;
		mRequest = request;
		mResponse = response;
	}

	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public ListPostsResource listByModificationDate(
		@PathParam("start") int start, 	
		@PathParam("limit") int limit) throws IOException {
		List<PostEntity> posts = mPostsListModel.list(start, limit);
		if(posts==null) {
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} else {
			ListPostsResource adaptedPosts = new ListPostsResource(posts);
			return adaptedPosts;
		}
	}

  public static class ListPostsResource {

    public List<PostResource> posts = new ArrayList<PostResource>();
		public ListPostsResource(List<PostEntity> postsEntities) {
			List<PostResource> postsResources = new ArrayList<PostResource>();
			for (PostEntity p: postsEntities) {
				PostResource postResource = new PostResource(p.getUsername(), 
						p.getCreated(), 
						p.getModified(), 
						p.getSubject(), 
						p.getContent(),
						p.getTags(), 
						p.isAdminEdited());
				postResource.id = p.getId();
				postResource.threadId = p.getThreadId();
				postsResources.add(postResource);
			}
			posts = postsResources;
		}
  }


}

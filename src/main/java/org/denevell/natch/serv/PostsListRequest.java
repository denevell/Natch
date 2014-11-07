package org.denevell.natch.serv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.model.PostEntity;
import org.denevell.natch.model.PostsListByModDateModel;
import org.denevell.natch.serv.PostSingleRequest.PostResource;

@Path("post")
public class PostsListRequest {
	
	@Context HttpServletRequest mRequest;
	@Inject PostsListByModDateModel mPostsListModel;
	
	public PostsListRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public PostsListRequest(PostsListByModDateModel model, HttpServletRequest request) {
		mPostsListModel = model;
		mRequest = request;
	}

	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public ListPostsResource listByModificationDate(
		@PathParam("start") int start, 	
		@PathParam("limit") int limit) throws IOException {
		List<PostEntity> posts = mPostsListModel.list(start, limit);
		ListPostsResource adaptedPosts = new ListPostsResource(posts);
		return adaptedPosts;
	}

  public static class ListPostsResource {

    public List<PostResource> posts = new ArrayList<PostResource>();
    public ListPostsResource() {}
		public ListPostsResource(List<PostEntity> postsEntities) {
			List<PostResource> postsResources = new ArrayList<PostResource>();
			for (PostEntity p: postsEntities) {
				PostResource postResource = new PostResource(p.username, 
						p.created, 
						p.modified, 
						p.subject, 
						p.content,
						p.tags, 
						p.adminEdited);
				postResource.id = p.id;
				postResource.threadId = p.threadId;
				postsResources.add(postResource);
			}
			posts = postsResources;
		}
  }


}

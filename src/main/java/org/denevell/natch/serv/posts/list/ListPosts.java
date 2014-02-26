package org.denevell.natch.serv.posts.list;

import java.io.IOException;
import java.util.List;

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
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.utils.Log;

@Path("post")
public class ListPosts {
	
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private CallDbBuilder<PostEntity> mModel;
	
	public ListPosts() {
		mModel = new CallDbBuilder<PostEntity>();
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public ListPosts(CallDbBuilder<PostEntity> model, HttpServletRequest request, HttpServletResponse response) {
		mModel = model;
		mRequest = request;
		mResponse = response;
	}

	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public ListPostsResource listByModificationDate(
		@PathParam("start") int start, 	
		@PathParam("limit") int limit 	
			) throws IOException {
		List<PostEntity> posts = null;
		try {
			posts = mModel
				.start(start)
				.max(limit)
				.namedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE)
				.list(PostEntity.class);
		} catch(Exception e) {
			Log.error(getClass(), "Couldn't list posts: ", e);
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} 

		if(posts==null) {
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} else {
			ListPostsResource adaptedPosts = new ListPostsResourceAdapter(posts);
			return adaptedPosts;
		}
	}

}

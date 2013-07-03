package org.denevell.natch.serv.threads;

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

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.serv.posts.ListThreadsResourceAdapter;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.utils.Log;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("threads")
@Api(value="/post", description="Adds, Deletes, Edits and Lists posts")
public class ThreadsREST {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private PostsModel mModel;
	
	public ThreadsREST() {
		mModel = new PostsModel();
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public ThreadsREST(PostsModel postModel, HttpServletRequest request, HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}
	
	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists threads, mostly recently created first", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ListPostsResource listThreads(
		@ApiParam(name="start") @PathParam("start") int start, 	
		@ApiParam(name="limit") @PathParam("limit") int limit 	
			) throws IOException {
		List<ThreadEntity> threads = null;
		try {
			mModel.init();
			threads = mModel.listThreads(start, limit);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} finally {
			mModel.close();
		}
		if(threads==null) {
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} else {
			ListPostsResource adaptedPosts = new ListThreadsResourceAdapter(threads);
			return adaptedPosts;
		}
	}	
	
	@GET
	@Path("/{tag}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists threads by tag, mostly recently created first", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ListPostsResource listThreadsByTag(
			@ApiParam(name="tag") @PathParam("tag")  String tag,
		@ApiParam(name="start") @PathParam("start") int start, 	
		@ApiParam(name="limit") @PathParam("limit") int limit 	
			) throws IOException {
		List<ThreadEntity> threads = null;
		try {
			mModel.init();
			threads = mModel.listThreadsByTag(tag, start, limit);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} finally {
			mModel.close();
		}
		if(threads==null) {
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} else {
			ListPostsResource adaptedPosts = new ListThreadsResourceAdapter(threads);
			return adaptedPosts;
		}		
	}	

}

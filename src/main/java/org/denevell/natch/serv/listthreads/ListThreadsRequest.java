package org.denevell.natch.serv.listthreads;

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
import org.denevell.natch.io.threads.ListThreadsResource;
import org.denevell.natch.utils.Log;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("threads")
public class ListThreadsRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private ThreadModel mModel;
	
	public ListThreadsRequest() {
		mModel = new ThreadModel();
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public ListThreadsRequest(ThreadModel postModel, HttpServletRequest request, HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}
	
	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists threads, mostly recently created first", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ListThreadsResource listThreads(
		@ApiParam(name="start") @PathParam("start") int start, 	
		@ApiParam(name="limit") @PathParam("limit") int limit 	
			) throws IOException {
		List<ThreadEntity> threads = null;
		long num = -1;
		try {
			mModel.init();
			threads = mModel.listThreads(start, limit);
			num = mModel.getNumOfPosts();
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
			ListThreadsResource adaptedPosts = new ListThreadsResourceAdapter(threads);
			adaptedPosts.setNumOfThreads(num);
			return adaptedPosts;
		}
	}	
	
	@GET
	@Path("/{tag}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public ListThreadsResource listThreadsByTag(
			@PathParam("tag")  String tag,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit 	
			) throws IOException {
		List<ThreadEntity> threads = null;
		long num = -1;
		try {
			mModel.init();
			threads = mModel.listThreadsByTag(tag, start, limit);
			num = mModel.getNumOfPosts(tag);
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
			ListThreadsResource adaptedPosts = new ListThreadsResourceAdapter(threads);
			adaptedPosts.setNumOfThreads(num);
			return adaptedPosts;
		}		
	}	

}

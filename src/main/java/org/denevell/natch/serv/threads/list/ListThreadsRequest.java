package org.denevell.natch.serv.threads.list;

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
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.io.threads.ListThreadsResource;
import org.denevell.natch.utils.Log;

@Path("threads")
public class ListThreadsRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private CallDbBuilder<ThreadEntity> mModel;
	
	public ListThreadsRequest() {
		mModel = new CallDbBuilder<ThreadEntity>();
	}
	
	/**
	 * For DI testing.
	 */
	public ListThreadsRequest(CallDbBuilder<ThreadEntity> postModel, HttpServletRequest request, HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}

	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public ListThreadsResource listThreads(
		@PathParam("start") int start, 	
		@PathParam("limit") int limit 	
			) throws IOException {
		return listThreads(
				null, 
				start, 
				limit, 
				ThreadEntity.NAMED_QUERY_LIST_THREADS,
				ThreadEntity.NAMED_QUERY_COUNT_THREADS);
	}	
	
	@GET
	@Path("/{tag}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public ListThreadsResource listThreadsByTag(
			@PathParam("tag")  String tag,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit 	
			) throws IOException {
		return listThreads(
				tag, 
				start, 
				limit, 
				ThreadEntity.NAMED_QUERY_LIST_THREADS_BY_TAG,
				ThreadEntity.NAMED_QUERY_COUNT_THREAD_BY_TAG);
	}

	private ListThreadsResource listThreads(
			String tag, 
			int start, 
			int limit,
			String listQuery,
			String countQuery)
			throws IOException {
		List<ThreadEntity> threads = null;
		long num = -1;
		try {
			if(tag!=null) {
				mModel = mModel.queryParam("tag", tag);
			}
			threads = mModel
					.start(start)
					.max(limit)
					.namedQuery(listQuery)
					.list(ThreadEntity.class);
			num = mModel
					.namedQuery(countQuery)
					.count(ThreadEntity.class);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
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

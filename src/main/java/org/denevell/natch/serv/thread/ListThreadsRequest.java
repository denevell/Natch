package org.denevell.natch.serv.thread;

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

import org.denevell.natch.io.threads.ListThreadsResource;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.ThreadsListModel;
import org.denevell.natch.model.interfaces.ThreadsListModel.ThreadsAndNumTotalThreads;
import org.denevell.natch.utils.Log;

@Path("threads")
public class ListThreadsRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject ThreadsListModel mThreadsModel;
	
	public ListThreadsRequest() {
	}
	
	/**
	 * For DI testing.
	 */
	public ListThreadsRequest(ThreadsListModel threadModel, HttpServletRequest request, HttpServletResponse response) {
		mThreadsModel = threadModel;
		mRequest = request;
		mResponse = response;
	}

	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public ListThreadsResource listThreads(
		@PathParam("start") int start, 	
		@PathParam("limit") int limit) throws IOException {
		ThreadsAndNumTotalThreads threads = mThreadsModel.list(null, start, limit);
		return listThreads(threads);
	}	
	
	@GET
	@Path("/{tag}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public ListThreadsResource listThreadsByTag(
			@PathParam("tag")  String tag,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit) throws IOException {
		ThreadsAndNumTotalThreads threads = mThreadsModel.list(tag, start, limit);
		return listThreads(threads);
	}

	private ListThreadsResource listThreads(ThreadsAndNumTotalThreads threads) throws IOException {
		ListThreadsResource adaptedPosts = new ListThreadsResourceAdapter(threads.getThreads());
		adaptedPosts.setNumOfThreads(threads.getNumOfThreads());
		return adaptedPosts;
	}	

	public static class ListThreadsResourceAdapter extends ListThreadsResource {

		public ListThreadsResourceAdapter(List<ThreadEntity> threads) {
			List<ThreadResource> postsResources = new ArrayList<ThreadResource>();
			for (ThreadEntity p: threads) {
				if(p.getRootPost()==null) {
					reportNullRootThreadError(p);
					continue;
				} else {
					ThreadResource postResource = new ThreadResource();
					postResource.setAuthor(p.getRootPost().getUser().getUsername());
					postResource.setNumPosts((int) p.getNumPosts());
					postResource.setSubject(p.getRootPost().getSubject());
					postResource.setRootPostId(p.getRootPost().getId());
					postResource.setTags(p.getRootPost().getTags());
					postResource.setModification(p.getLatestPost().getModified());
					postResource.setCreation(p.getRootPost().getCreated());
					postResource.setId(p.getId());
					postResource.setLatestPostId(p.getLatestPost().getId());
					postsResources.add(postResource);
				}
			}
			setThreads(postsResources);
		}

		private void reportNullRootThreadError(ThreadEntity p) {
			if(p.getId()!=null) {
				Log.info(getClass(), "Found a thread with a null root post. Thread: " + p.getId());
			} else {
				Log.info(getClass(), "Found a thread with a null root post. Unknown thread id.");
			}
		}

	}
}

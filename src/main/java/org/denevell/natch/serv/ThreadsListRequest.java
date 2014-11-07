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

import org.denevell.natch.model.ThreadEntity;
import org.denevell.natch.model.ThreadsListModel;
import org.denevell.natch.model.ThreadsListModel.ThreadsAndNumTotalThreads;
import org.denevell.natch.serv.ThreadListRequest.ThreadResource;
import org.denevell.natch.utils.Log;

@Path("threads")
public class ThreadsListRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject ThreadsListModel mThreadsModel;
	
	public ThreadsListRequest() {
	}
	
	/**
	 * For DI testing.
	 */
	public ThreadsListRequest(ThreadsListModel threadModel, HttpServletRequest request, HttpServletResponse response) {
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
		ListThreadsResource adaptedPosts = new ListThreadsResourceAdapter(threads.getThreads());
		adaptedPosts.numOfThreads = (threads.getNumOfThreads());
		return adaptedPosts;
	}	
	
	@GET
	@Path("/{tag}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public ListThreadsResource listThreadsByTag(
			@PathParam("tag")  String tag,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit) throws IOException {
		ThreadsAndNumTotalThreads threads = mThreadsModel.list(tag, start, limit);
		ListThreadsResource adaptedPosts = new ListThreadsResourceAdapter(threads.getThreads());
		adaptedPosts.numOfThreads = (threads.getNumOfThreads());
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
					postResource.author = (p.getRootPost().username);
					postResource.numPosts = ((int) p.getNumPosts());
					postResource.subject = (p.getRootPost().getSubject());
					postResource.rootPostId = (p.getRootPost().id);
					postResource.tags = (p.getRootPost().getTags());
					postResource.modification = (p.getLatestPost().modified);
					postResource.creation = (p.getRootPost().created);
					postResource.id = (p.getId());
					postResource.latestPostId = (p.getLatestPost().id);
					postsResources.add(postResource);
				}
			}
			this.threads = (postsResources);
		}

		private void reportNullRootThreadError(ThreadEntity p) {
			if(p.getId()!=null) {
				Log.info(getClass(), "Found a thread with a null root post. Thread: " + p.getId());
			} else {
				Log.info(getClass(), "Found a thread with a null root post. Unknown thread id.");
			}
		}

	}

  public static class ListThreadsResource {
    public long numOfThreads;
    public List<ThreadResource> threads = new ArrayList<ThreadResource>();

  }
}

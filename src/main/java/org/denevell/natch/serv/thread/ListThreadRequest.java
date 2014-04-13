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

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.ThreadListModel;
import org.denevell.natch.model.interfaces.ThreadListModel.ThreadAndPosts;

@Path("post/thread")
public class ListThreadRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject ThreadListModel mThreadModel;
	
	public ListThreadRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public ListThreadRequest(
			CallDbBuilder<PostEntity> postModel, 
			CallDbBuilder<ThreadEntity> threadModel,
			HttpServletRequest request, HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}
		
	@GET
	@Path("/{threadId}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public ThreadResource listByThreadId(
			@PathParam("threadId") String threadId,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit) throws IOException {
		ThreadAndPosts ret = mThreadModel.list(threadId, start, limit);
		if(ret==null) {
			mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} else {
			ThreadResource adaptedPosts = adaptPosts(ret.getPosts(), ret.getThreadEntity());
			return adaptedPosts;
		}
	}

	private ThreadResource adaptPosts(
			List<PostEntity> posts, 
			ThreadEntity thread) {
		ThreadResource tr =  new ThreadResource();
		List<PostResource> postsResources = new ArrayList<PostResource>();
		for (PostEntity p: posts) {
			PostResource postResource = new PostResource(p.getUser().getUsername(), 
					p.getCreated(), 
					p.getModified(), 
					p.getSubject(), 
					p.getContent(),
					p.getTags(), 
					p.isAdminEdited());
			postResource.setId(p.getId());
			postResource.setThreadId(p.getThreadId());
			postsResources.add(postResource);
		}
		tr.setSubject(thread.getRootPost().getSubject());
		tr.setAuthor(thread.getRootPost().getUser().getUsername());
		tr.setPosts(postsResources);
		tr.setNumPosts((int) thread.getNumPosts());
		tr.setId(thread.getId());
		tr.setTags(thread.getRootPost().getTags());
		return tr;
	}	

}

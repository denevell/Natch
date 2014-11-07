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
import org.denevell.natch.model.ThreadEntity;
import org.denevell.natch.model.ThreadListModel;
import org.denevell.natch.model.ThreadListModel.ThreadAndPosts;
import org.denevell.natch.serv.PostSingleRequest.PostResource;

@Path("post/thread")
public class ThreadListRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject ThreadListModel mThreadModel;
	
	public ThreadListRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public ThreadListRequest(
			ThreadListModel model,
			HttpServletRequest request, HttpServletResponse response) {
		mThreadModel = model;
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
		tr.subject = thread.getRootPost().getSubject();
		tr.author = thread.getRootPost().getUsername();
		tr.posts = postsResources;
		tr.numPosts = (int) thread.getNumPosts();
		tr.id = thread.getId();
		tr.tags = thread.getRootPost().getTags();
		return tr;
	}	

  public static class ThreadResource {

    public List<String> tags;
    public String id;
    public String subject;
    public String author;
    public int numPosts;
    public long creation;
    public long modification;
    public long rootPostId;
    public long latestPostId;
    public List<PostResource> posts = new ArrayList<PostResource>();

    public ThreadResource(ThreadResource tr) {
      subject = tr.subject;
      author = tr.author;
      numPosts = tr.numPosts;
      posts = tr.posts;
      tags = tr.tags;
      rootPostId = tr.rootPostId;
      latestPostId = tr.latestPostId;
    }

    public ThreadResource(String subject, String author, long creation, long modification, List<String> tags) {
      this.tags = tags;
      this.subject = subject;
      this.author = author;
      this.creation = creation;
      this.modification = modification;
    }

    public ThreadResource() {
    }

  }

}

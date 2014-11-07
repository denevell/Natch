package org.denevell.natch.serv;

import java.io.IOException;
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
import org.denevell.natch.model.PostSingleModel;

@Path("post/single")
public class PostSingleRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostSingleModel mPostSingle;
	
	public PostSingleRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public PostSingleRequest(PostSingleModel model, HttpServletRequest request, HttpServletResponse response) {
		mPostSingle = model;
		mRequest = request;
		mResponse = response;
	}

	@GET
	@Path("{postId}")
	@Produces(MediaType.APPLICATION_JSON)
	public PostResource findById(@PathParam("postId") long postId) throws IOException {
		PostEntity post = mPostSingle.find(postId);
		if(post==null) {
			mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		PostResource postResource = new PostResource(
				post.getUsername(), 
				post.getCreated(), 
				post.getModified(),
				post.getSubject(), 
				post.getContent(), 
				post.getTags(),
				post.isAdminEdited());
		postResource.id = post.getId();
		postResource.threadId = post.getThreadId();
		return postResource;
	}
	
	public static class PostResource {

	  public long id;
	  public String username;
	  public String subject;
	  public String content;
	  public String threadId;
	  public long creation;
	  public long modification;
	  public List<String> tags;
	  public boolean adminEdited;
	  
	  public PostResource() {
	  }
	  
	  public PostResource(String username, long created, long modified, String subject, String content, List<String> tags, boolean adminEdit) {
	    this.username = username;
	    this.creation = created;
	    this.modification = modified;
	    this.subject = subject;
	    this.content = content;
	    this.tags = tags;
	    this.adminEdited = adminEdit;
	  }
	  
	  public PostResource(PostResource post) {
	    this(post.username, post.creation, post.modification, post.subject, post.content, post.tags, post.adminEdited);
	    this.threadId = post.threadId;
	  }
	  
	}


}

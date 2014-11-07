package org.denevell.natch.serv;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.model.PostEntity;
import org.denevell.natch.model.PostSingleModel;

@Path("post/single")
public class PostSingleRequest {
	
	@Context HttpServletResponse mResponse;
	@Inject PostSingleModel mPostSingle;
	
	@GET
	@Path("{postId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("postId") long postId) throws IOException {
		PostEntity post = mPostSingle.find(postId);
		if(post==null) {
		  return Response.status(404).build();
		}
	  return Response.ok().entity(new PostResource(post)).build();
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
	  
	  public PostResource() {}
	  
	  public PostResource(PostEntity post) {
	    this.username = post.username;
	    this.creation = post.created;
	    this.modification = post.modified;
	    this.subject = post.subject;
	    this.content = post.content;
	    this.tags = post.tags;
	    this.adminEdited = post.adminEdited;
	    this.id = post.id;
	    this.threadId = post.threadId;
	  }
	  
	}


}

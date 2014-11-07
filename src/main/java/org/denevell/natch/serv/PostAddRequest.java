package org.denevell.natch.serv;

import java.util.Date;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.model.PostAddModel;
import org.denevell.natch.model.PostEntity;
import org.denevell.natch.model.ThreadEntity;
import org.denevell.natch.model.UserGetLoggedInModel;
import org.denevell.natch.model.UserGetLoggedInModel.User;
import org.hibernate.validator.constraints.NotBlank;

@Path("post/add")
public class PostAddRequest {
	
	@Context UriInfo mInfo;
	@Context ServletContext context;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Inject PostAddModel mAddPostModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPost(@Valid PostAddInput input) {
		User userEntity = (User) mRequest.getAttribute("user");
    PostEntity pe = new PostEntity();
    long created = new Date().getTime();
    pe.setContent(input.content);
    pe.setThreadId(input.threadId);
    pe.setUsername(userEntity.username);
    pe.setCreated(created);
    pe.setModified(created);
	  ThreadEntity okay = mAddPostModel.add(pe);
	  if(okay==null) {
	    return Response.serverError().build();
	  } else {
	    return Response.ok().build();
	  }
	}
	
  public static class PostAddInput {
    @NotBlank(message="Post must have content")
    public String content;
    @NotBlank(message="Post must include thread id")
    public String threadId;
  }

}

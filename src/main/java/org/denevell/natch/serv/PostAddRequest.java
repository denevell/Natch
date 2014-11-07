package org.denevell.natch.serv;

import java.util.Date;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.model.PostAddModel;
import org.denevell.natch.model.PostEntity;
import org.denevell.natch.model.ThreadEntity;
import org.denevell.natch.model.UserGetLoggedInModel.User;
import org.hibernate.validator.constraints.NotBlank;

@Path("post/add")
public class PostAddRequest {
	
	@Context HttpServletRequest mRequest;
	@Inject PostAddModel mAddPostModel;
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPost(@Valid PostAddInput input) {
		User userEntity = (User) mRequest.getAttribute("user");
    PostEntity pe = createThreadEntity(input, userEntity);
	  ThreadEntity okay = mAddPostModel.add(pe);
	  if(okay==null) {
	    return Response.serverError().build();
	  } else {
	    return Response.ok().build();
	  }
	}

  private PostEntity createThreadEntity(PostAddInput input, User userEntity) {
    PostEntity pe = new PostEntity();
    long created = new Date().getTime();
    pe.content = input.content;
    pe.threadId = input.threadId;
    pe.username = (userEntity.username);
    pe.created = (created);
    pe.modified = (created);
    return pe;
  }
	
  public static class PostAddInput {
    @NotBlank(message="Post must have content")
    public String content;
    @NotBlank(message="Post must include thread id")
    public String threadId;
  }

}

package org.denevell.natch.serv;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.model.PostEditModel;
import org.denevell.natch.model.PostEntity;
import org.denevell.natch.model.UserGetLoggedInModel.User;
import org.hibernate.validator.constraints.NotBlank;

@Path("post/editpost")
public class PostEditRequest {
	
	@Context HttpServletRequest mRequest;
	@Inject PostEditModel mPostEditModel;
	
	@POST
	@Path("{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public Response editpost(
			@PathParam(value="postId") long postId, 
			@Valid PostEditInput editPostResource) {
		User userEntity = (User) mRequest.getAttribute("user");
		PostEntity editData = new PostEntity();
		editData.content = (editPostResource.content);
		editData.subject = ("-");
		int result = mPostEditModel.edit(postId, userEntity.username, editData, userEntity.admin);
		if(result == PostEditModel.EDITED) {
		  return Response.ok().build();
		} else if(result == PostEditModel.DOESNT_EXIST) {
		  return Response.status(404).build();
		} else if(result == PostEditModel.NOT_YOURS) {
		  return Response.status(403).build();
		} else {
		  return Response.serverError().build();
		}
	}

  public static class PostEditInput {
    @NotBlank(message="Post must have content")
    public String content;
  }

}
package org.denevell.natch.serv;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.model.PostEditModel;
import org.denevell.natch.model.PostEntity;
import org.denevell.natch.model.UserGetLoggedInModel;
import org.denevell.natch.model.UserGetLoggedInModel.User;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Path("post/editpost")
public class PostEditRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostEditModel mPostEditModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	
	public PostEditRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public PostEditRequest(HttpServletRequest request, HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}
	
	@POST
	@Path("{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public Response editpost(
			@PathParam(value="postId") long postId, 
			@Valid EditPostResource editPostResource) {
		User userEntity = (User) mRequest.getAttribute("user");
		PostEntity editData = new PostEntity();
		editData.setContent(editPostResource.content);
		editData.setSubject("-");
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

  public static class EditPostResource {
    @NotEmpty
    @NotBlank
    private String content;
  }

}
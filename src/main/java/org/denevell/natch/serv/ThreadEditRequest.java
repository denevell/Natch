package org.denevell.natch.serv;

import java.util.List;

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

@Path("post/editthread")
public class ThreadEditRequest {
	
	public final static String EDITED = "edited";
	public final static String DELETED = "deleted";
	public final static String ADDED = "added";
	public final static String DOESNT_EXIST = "doesntexist";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	public final static String NOT_YOURS_TO_DELETE = "notyourtodelete";
  private static final int TAG_TOO_LARGE = 105;
  private static final int SUBJECT_TOO_LARGE = 106;		
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostEditModel mPostEditModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	
	//TODO: What about the 400s?
	
	public ThreadEditRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public ThreadEditRequest(HttpServletRequest request, HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}
	
	@POST
	@Path("{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public Response editpost(
			@PathParam(value="postId") long postId, 
			@Valid EditThreadResource editPostResource) {
		int result = edit(postId, editPostResource);
		if(result == PostEditModel.EDITED) {
		  return Response.ok().build();
		} else if(result == PostEditModel.DOESNT_EXIST) {
		  return Response.status(404).build();
		} else if(result == SUBJECT_TOO_LARGE) {
		  return Response.status(400).build();
		} else if(result == PostEditModel.NOT_YOURS) {
		  return Response.status(403).build();
		} else if(result == TAG_TOO_LARGE) {
		  return Response.status(400).build();
		} else {
		  return Response.serverError().build();
		}
	}

	private int edit(long postId, EditThreadResource editPostResource) {
		User userEntity = (User) mRequest.getAttribute("user");
		PostEntity editingData = new PostEntity();
		editingData.subject = (editPostResource.subject);
		editingData.content = (editPostResource.content);
		editingData.tags = (editPostResource.tags);
		int result = mPostEditModel.edit(postId, userEntity.username, editingData, userEntity.admin);
		return result;
	}
	
	public static class EditThreadResource {
	  @NotEmpty @NotBlank public String content;
	  @NotEmpty @NotBlank public String subject;
	  public List<String> tags;
	}

}
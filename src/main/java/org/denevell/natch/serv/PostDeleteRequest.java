package org.denevell.natch.serv;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.model.PostDeleteModel;
import org.denevell.natch.model.UserGetLoggedInModel;
import org.denevell.natch.model.UserGetLoggedInModel.User;

@Path("post/del")
public class PostDeleteRequest {
	
	public final static String EDITED = "edited";
	public final static String ADDED = "added";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostDeleteModel mPostFindModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	
	public PostDeleteRequest() {
	}
	
	/**
	 * For DI testing.
	 */
	public PostDeleteRequest(
			HttpServletRequest request, 
			HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}
	
	@DELETE
	@Path("{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("postId") long number) {
		User userEntity = (User) mRequest.getAttribute("user");
		int result = mPostFindModel.delete(number, userEntity.username, userEntity.admin);
		if(result == PostDeleteModel.DELETED) {
		  return Response.ok().build();
		} else if(result == PostDeleteModel.DOESNT_EXIST) {
		  return Response.status(404).build();
		} else if(result == PostDeleteModel.NOT_YOURS) {
		  return Response.status(403).build();
		} else {
		  return Response.serverError().build();
		}
	}

}

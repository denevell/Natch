package org.denevell.natch.serv;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.model.PostDeleteModel;
import org.denevell.natch.model.UserGetLoggedInModel.User;

@Path("post/del")
public class PostDeleteRequest {
	
	@Context HttpServletRequest mRequest;
	@Inject PostDeleteModel mPostFindModel;
	
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

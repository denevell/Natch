package org.denevell.natch.serv;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.model.PostAddModel;
import org.denevell.natch.model.PostDeleteModel;
import org.denevell.natch.model.PostEditModel;
import org.denevell.natch.model.PostEntity;
import org.denevell.natch.model.PostEntity.AddInput;
import org.denevell.natch.model.PostEntity.EditInput;
import org.denevell.natch.model.PostEntity.Output;
import org.denevell.natch.model.PostEntity.OutputList;
import org.denevell.natch.model.PostSingleModel;
import org.denevell.natch.model.PostsListByModDateModel;
import org.denevell.natch.model.ThreadEntity;
import org.denevell.natch.model.UserGetLoggedInModel.User;

@Path("post")
public class PostRequests {

  @Context HttpServletRequest mRequest;
	@Inject PostAddModel mAddPostModel;
	@Inject PostDeleteModel mPostFindModel;
	@Inject PostEditModel mPostEditModel;
	@Inject PostSingleModel mPostSingle;
	@Inject PostsListByModDateModel mPostsListModel;

  @GET
	@Path("single/{postId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("postId") long postId) throws IOException {
		PostEntity post = mPostSingle.find(postId);
		if(post==null) {
		  return Response.status(404).build();
		}
	  return Response.ok().entity(new Output(post)).build();
	}

	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public OutputList listByModificationDate(
		@PathParam("start") int start, 	
		@PathParam("limit") int limit) throws IOException {
		return new OutputList(mPostsListModel.list(start, limit));
	}
	
	@PUT
	@Path("add")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPost(@Valid AddInput input) {
		User userEntity = (User) mRequest.getAttribute("user");
	  ThreadEntity thread = mAddPostModel.add(input.adapt(userEntity.username));
	  if(thread == null) {
	    return Response.serverError().build();
	  } else {
	    return Response.ok().build();
	  }
	}

	@DELETE
	@Path("del/{postId}") 
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

	@POST
	@Path("editpost/{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public Response editpost(
			@PathParam(value="postId") long postId, 
			@Valid EditInput input) {
		User userEntity = (User) mRequest.getAttribute("user");
		int result = mPostEditModel.edit(postId, userEntity.username, input.adapt(), userEntity.admin);
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

}

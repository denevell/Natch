package org.denevell.natch.serv;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity.EditInput;
import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.model.PostEditModel;
import org.denevell.natch.model.PostSingleModel;
import org.denevell.natch.model.PostsListByModDateModel;
import org.denevell.natch.model.UserGetLoggedInModel.User;

@Path("post")
public class PostRequests {

  @Context HttpServletRequest mRequest;
	@Inject PostEditModel mPostEditModel;
	@Inject PostSingleModel mPostSingle;
	@Inject PostsListByModDateModel mPostsListModel;

  @GET
	@Path("single/{postId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("postId") long postId) throws IOException {
		return mPostSingle.find(postId).httpReturn();
	}

	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public OutputList listByModificationDate(
	    @PathParam("start") int start, 	
	    @PathParam("limit") int limit) throws IOException {
		return new OutputList(mPostsListModel.list(start, limit));
	}

  @POST
  @Path("editpost/{postId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response editpost(
      @PathParam(value = "postId") long postId, 
      @Valid EditInput input) {
    User user = (User) mRequest.getAttribute("user");
    return mPostEditModel.edit(postId, user.username, input.adapt(), user.admin).httpReturn();
  }

}

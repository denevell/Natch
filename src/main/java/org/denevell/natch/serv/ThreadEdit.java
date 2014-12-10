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

import org.denevell.natch.entities.ThreadEntity.EditInput;
import org.denevell.natch.serv.PostEdit.PostEditService;
import org.denevell.natch.utils.UserGetLoggedInService.User;

@Path("thread_edit/{postId}")
public class ThreadEdit {

  @Context HttpServletRequest mRequest;
  @Inject PostEditService mPostEditService;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response editThread(
			@PathParam("postId") long postId, 
			@Valid EditInput input) {
		User user = (User) mRequest.getAttribute("user");
		return mPostEditService.edit(postId, input.adapt(), user.username, user.admin);
	}

}

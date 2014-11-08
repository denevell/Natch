package org.denevell.natch.serv;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.model.PostAddModel;
import org.denevell.natch.model.PushSendModel;
import org.denevell.natch.model.ThreadEntity;
import org.denevell.natch.model.ThreadEntity.AddInput;
import org.denevell.natch.model.ThreadEntity.Output;
import org.denevell.natch.model.ThreadListModel;
import org.denevell.natch.model.ThreadListModel.ThreadAndPosts;
import org.denevell.natch.model.UserGetLoggedInModel.User;

@Path("thread")
public class ThreadRequests {
	
	@Context HttpServletResponse mResponse;
	@Context HttpServletRequest mRequest;
	@Inject ThreadListModel mThreadModel;
	@Inject PostAddModel mAddPostModel;

	@GET
	@Path("/{threadId}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public Output listByThreadId(
			@PathParam("threadId") String threadId,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit) throws IOException {
		ThreadAndPosts ret = mThreadModel.list(threadId, start, limit);
		if(ret==null) {
			mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} else {
			return new Output(ret.getPosts(), ret.getThreadEntity());
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addThread(@Valid AddInput input) {
    User userEntity = (User) mRequest.getAttribute("user");
    ThreadEntity thread = mAddPostModel.add(input.adapt(false, userEntity.username));
    if (thread == null) {
      return Response.serverError().build();
    }
    PushSendModel.sendPushNotifications(thread);
    return Response.ok().build();
	}	

}

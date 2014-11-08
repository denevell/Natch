package org.denevell.natch.serv;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
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
import org.denevell.natch.model.PostEditModel;
import org.denevell.natch.model.PushSendModel;
import org.denevell.natch.model.ThreadEntity;
import org.denevell.natch.model.ThreadEntity.AddFromPostInput;
import org.denevell.natch.model.ThreadEntity.AddInput;
import org.denevell.natch.model.ThreadEntity.EditInput;
import org.denevell.natch.model.ThreadEntity.Output;
import org.denevell.natch.model.ThreadEntity.OutputList;
import org.denevell.natch.model.ThreadFromPostModel;
import org.denevell.natch.model.ThreadListModel;
import org.denevell.natch.model.ThreadListModel.ThreadAndPosts;
import org.denevell.natch.model.ThreadsListModel;
import org.denevell.natch.model.ThreadsListModel.ThreadsAndNumTotalThreads;
import org.denevell.natch.model.UserGetLoggedInModel.User;

@Path("thread")
public class ThreadRequests {
	
	@Context HttpServletResponse mResponse;
	@Context HttpServletRequest mRequest;
	@Inject ThreadListModel mThreadModel;
	@Inject PostAddModel mAddPostModel;
	@Inject PostEditModel mPostEditModel;
	@Inject ThreadsListModel mThreadsModel;
	@Inject ThreadFromPostModel mThreadFromPostModel;
	
	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public OutputList listThreads(
		@PathParam("start") int start, 	
		@PathParam("limit") int limit) throws IOException {
		return new OutputList(mThreadsModel.list(null, start, limit).getThreads());
	}	

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
	
	@GET
	@Path("bytag/{tag}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public OutputList listThreadsByTag(
			@PathParam("tag")  String tag,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit) throws IOException {
		ThreadsAndNumTotalThreads threads = mThreadsModel.list(tag, start, limit);
		return new OutputList(threads.getThreads());
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
	
	@PUT
	@Path("frompost")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
  public Response addThreadFromPost(@Valid AddFromPostInput input) throws IOException {
    User userEntity = (User) mRequest.getAttribute("user");
    if (!userEntity.admin) {
      return Response.status(403).build();
    }
    ThreadEntity thread = mThreadFromPostModel.makeNewThread(input.postId, input.subject);
    if(thread==null) {
      return Response.serverError().build();
    }
    return Response.ok().build();
  }

	@POST
	@Path("{postId}") 
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

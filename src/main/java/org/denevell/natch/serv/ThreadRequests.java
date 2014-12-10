package org.denevell.natch.serv;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.ThreadEntity.OutputList;
import org.denevell.natch.model.ThreadsListModel;
import org.denevell.natch.model.ThreadsListModel.ThreadsAndNumTotalThreads;

@Path("thread")
public class ThreadRequests {
	
	@Context HttpServletResponse mResponse;
	@Context HttpServletRequest mRequest;
	@Inject ThreadsListModel mThreadsModel;
	
	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public Response listThreads(
		@PathParam("start") int start, 	
		@PathParam("limit") int limit) throws IOException {
    ThreadsAndNumTotalThreads list = mThreadsModel.list(null, start, limit);
    return Response.ok().entity(new OutputList(list)).build();
	}	

	@GET
	@Path("bytag/{tag}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	public OutputList listThreadsByTag(
			@PathParam("tag")  String tag,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit) throws IOException {
		return new OutputList(mThreadsModel.list(tag, start, limit));
	}

}

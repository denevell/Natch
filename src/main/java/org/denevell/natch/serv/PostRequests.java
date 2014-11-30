package org.denevell.natch.serv;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.model.PostsListByModDateModel;

@Path("post")
public class PostRequests {

  @Context HttpServletRequest mRequest;
	@Inject PostsListByModDateModel mPostsListModel;

	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	public OutputList listByModificationDate(
	    @PathParam("start") int start, 	
	    @PathParam("limit") int limit) throws IOException {
		return new OutputList(mPostsListModel.list(start, limit));
	}

}

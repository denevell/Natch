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
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("post_single/{postId}")
public class PostSingle {

  @Context HttpServletRequest mRequest;
  @Inject PostSingleService mPostSingle;

  @GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("postId") long postId) throws IOException {
		return mPostSingle.find(postId);
	}

  public static interface PostSingleService {
    default Response find(long postId) {
      return Jrappy2.find(JPAFactoryContextListener.sFactory, postId, false, PostEntity.class);
    }
  }

}

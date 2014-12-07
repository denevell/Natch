package org.denevell.natch.serv;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.PostEntity.OutputList;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("posts/{start}/{limit}")
public class PostsList {

  @Context HttpServletRequest mRequest;
  @Inject PostsListService mPostsListService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public OutputList listByModificationDate(
	    @PathParam("start") int start, 	
	    @PathParam("limit") int limit) throws IOException {
		return new OutputList(mPostsListService.list(start, limit));
	}

  public static interface PostsListService {
    default List<PostEntity> list(int start, int limit) {
      return Jrappy2.list(JPAFactoryContextListener.sFactory, 
          start, 
          limit, 
          "modified", 
          PostEntity.class);
    }
  }

}

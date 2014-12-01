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
      return null;//Jrappy2.find(JPAFactoryContextListener.sFactory, postId, false, PostEntity.class);
      /*
        List<PostEntity> posts = mPostModel.startTransaction().start(start).max(numResults)
            .namedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE).list(PostEntity.class);
            */
    }
  }

}

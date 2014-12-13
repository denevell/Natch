package org.denevell.natch.serv;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.entities.ThreadEntity.OutputList;
import org.denevell.natch.serv.ThreadsList.ThreadsAndNumTotalThreads;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("threads_bytag/{tag}/{start}/{limit}")
public class ThreadsListByTag {

  @Context HttpServletRequest mRequest;
  @Inject ThreadsListByTagService mThreadsListByTagService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	public OutputList listThreadsByTag(
			@PathParam("tag")  String tag,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit) throws IOException {
    return new OutputList(mThreadsListByTagService.list(start, limit, tag));
	}	

  public static interface ThreadsListByTagService {
    default ThreadsAndNumTotalThreads list(int start, int limit, String tag) {
      List<ThreadEntity> list = Jrappy2.list(JPAFactoryContextListener.sFactory, 
          start, 
          limit, 
          null, 
          ThreadEntity.class, 
          (cb, q) -> {
            Root<ThreadEntity> from = q.from(ThreadEntity.class);
            q.where(cb.isMember(tag, from.get("rootPost").get("tags")));
          });
      long count = Jrappy2.count(JPAFactoryContextListener.sFactory, ThreadEntity.class);
      return new ThreadsAndNumTotalThreads(list, count);
    }
  }

}

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
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.entities.ThreadEntity.OutputList;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("threads/{start}/{limit}")
public class ThreadsList {

  @Context HttpServletRequest mRequest;
  @Inject ThreadsListService mThreadsListService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	public Response listThreads(
		@PathParam("start") int start, 	
		@PathParam("limit") int limit) throws IOException {
    return Response.ok().entity(new OutputList(mThreadsListService.list(start, limit))).build();
	}	

  public static interface ThreadsListService {
    default ThreadsAndNumTotalThreads list(int start, int limit) {
      List<ThreadEntity> list = Jrappy2.list(JPAFactoryContextListener.sFactory, 
          start, 
          limit, 
          null, 
          ThreadEntity.class);
      long count = Jrappy2.count(JPAFactoryContextListener.sFactory, ThreadEntity.class);
      return new ThreadsAndNumTotalThreads(list, count);
    }
  }

  public static class ThreadsAndNumTotalThreads {
    private long numOfThreads;
    private List<ThreadEntity> threads;

    public ThreadsAndNumTotalThreads(List<ThreadEntity> threadPosts, long numOfThreads) {
      this.numOfThreads = numOfThreads;
      this.threads = threadPosts;
    }

    public long getNumOfThreads() {
      return numOfThreads;
    }

    public void setNumOfThreads(long numOfThreads) {
      this.numOfThreads = numOfThreads;
    }

    public List<ThreadEntity> getThreads() {
      return threads;
    }

    public void setThreads(List<ThreadEntity> threads) {
      this.threads = threads;
    }
  }


}

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

import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("thread_single/{threadId}/{start}/{limit}")
public class ThreadSingle {

  @Context HttpServletRequest mRequest;
	@Inject ThreadSingleService mThreadModel;

	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	public ThreadEntity byThreadId(
			@PathParam("threadId") String threadId,
			@PathParam("start") int start, 	
			@PathParam("limit") int limit) throws IOException {
		return mThreadModel.find(threadId, start, limit);
	}
	

  public static interface ThreadSingleService {
    default ThreadEntity find(String threadId, int start, int limit) {
      ThreadEntity thread = Jrappy2.findObject(JPAFactoryContextListener.sFactory, 
          threadId,
          false,
          ThreadEntity.class);
      int toIndex = start+limit;
      if(thread.posts.size()<toIndex) {
        toIndex = thread.posts.size();
      }
      thread.posts = Lists.newArrayList(thread.posts).subList(start, toIndex);
      return thread;
    }
  }

}

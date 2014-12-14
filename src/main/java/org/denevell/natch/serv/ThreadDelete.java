package org.denevell.natch.serv;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;
import org.denevell.natch.utils.UserGetLoggedInService.User;

@Path("thread_delete/{thread_id}")
public class ThreadDelete {
	
  @Context HttpServletRequest mRequest;
	@Inject ThreadDeleteService mThreadDeleteService;

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteThread(@PathParam("thread_id") String primaryKey) {
    User userEntity = (User) mRequest.getAttribute("user");
    return mThreadDeleteService.threadDelete(primaryKey, userEntity.username, userEntity.admin);
	}	

	public static interface ThreadDeleteService {
	  default Response threadDelete(Object primaryKey, String username, boolean admin) {
	    return Jrappy2.remove(JPAFactoryContextListener.sFactory, 
	        primaryKey, 
	        (thread) -> {
	          return admin || thread.rootPost.username.equals(username);
	        },
	        ThreadEntity.class);
	  }
  }
	
}

package org.denevell.natch.serv;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.entities.ThreadEntity.AddInput;
import org.denevell.natch.model.UserGetLoggedInModel.User;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("thread_add")
public class ThreadAdd {
	
  @Context HttpServletRequest mRequest;
	@Inject ThreadAddService mThreadAddService;

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addThread(@Valid AddInput input) {
    User userEntity = (User) mRequest.getAttribute("user");
    return mThreadAddService.threadAdd(input.adapt(userEntity.username));
	}	
	
	public static interface ThreadAddService {
	  default Response threadAdd(ThreadEntity threadEntity) {
	    return Jrappy2.persist(JPAFactoryContextListener.sFactory, threadEntity);
	  }
  }
	
}

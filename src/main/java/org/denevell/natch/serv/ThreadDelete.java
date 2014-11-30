package org.denevell.natch.serv;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.model.UserGetLoggedInModel.User;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("thread_add")
public class ThreadDelete {

  @Context HttpServletRequest mRequest;
  @Inject PostDeleteService mPostDeleteService;

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("postId") long number, @PathParam("threadId") String threadId) {
		User userEntity = (User) mRequest.getAttribute("user");
		return mPostDeleteService.delete(threadId, number, userEntity.admin);
	}

  public static interface PostDeleteService {
    default Response delete(String threadId, long postId, boolean admin) {
      return Jrappy2.update(
          JPAFactoryContextListener.sFactory, 
          ThreadEntity.class, 
          threadId, 
          (threadEntity) -> {
            threadEntity.posts = threadEntity.posts.stream()
                .filter(post -> post.id!=postId)
                .collect(Collectors.toList());
            return threadEntity;
          });
    }
  }

}

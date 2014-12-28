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

import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;
import org.denevell.natch.utils.UserGetLoggedInService.User;

@Path("post_delete/{postId}/{threadId}")
public class PostDelete {

  @Context HttpServletRequest mRequest;
  @Inject PostDeleteService mPostDeleteService;

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("postId") long number, @PathParam("threadId") String threadId) {
		User userEntity = (User) mRequest.getAttribute("user");
		return mPostDeleteService.delete(threadId, number, userEntity.username, userEntity.admin);
	}

  public static interface PostDeleteService {
    default Response delete(String threadId, long postId, String username, boolean admin) {
      return Jrappy2.update( 
          JPAFactoryContextListener.sFactory, 
          ThreadEntity.class, 
          threadId, 
          (threadEntity) -> { // Is post there
            return Lists.newArrayList(threadEntity.posts).stream()
                .filter(post -> post.id==postId)
                .count()==1;
          },
          (threadEntity) -> { // Do we have access rights
            return admin ||  
                Lists.newArrayList(threadEntity.posts).stream()
                .filter(post -> post.id==postId)
                .findFirst()
                .get().username.equals(username);
          },
          (threadEntity) -> { // Remove the post from thread
            threadEntity.posts = Lists.newArrayList(threadEntity.posts).stream()
                .filter(post -> post.id!=postId)
                .collect(Collectors.toList());
            if(threadEntity.posts.size()>1) 
              threadEntity.latestPost = threadEntity.posts.get(threadEntity.posts.size()-1);
            else if(threadEntity.posts.size()!=0) 
              threadEntity.latestPost = threadEntity.posts.get(0);
            return threadEntity;
          });
    }
  }

}

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

import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.entities.PostEntity.AddInput;
import org.denevell.natch.model.UserGetLoggedInModel.User;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("post_add")
public class PostAdd {

  @Context HttpServletRequest mRequest;
  @Inject PostAddService mPostAddService;

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addPost(@Valid AddInput input) {
    User userEntity = (User) mRequest.getAttribute("user");
    return mPostAddService.add(input.adapt(userEntity.username));
  }

  public static interface PostAddService {
    default Response add(PostEntity postEntity) {
      return Jrappy2.update(
          JPAFactoryContextListener.sFactory, 
          ThreadEntity.class, 
          postEntity.threadId, 
          (threadEntity) -> {
            postEntity.subject = threadEntity.rootPost.subject;
            threadEntity.latestPost = postEntity;
            threadEntity.posts.add(postEntity);
            threadEntity.numPosts = threadEntity.numPosts + 1;
            return threadEntity;
          });
    }
  }

}

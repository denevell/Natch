package org.denevell.natch.serv;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.entities.ThreadEntity.AddFromPostInput;
import org.denevell.natch.serv.PostDelete.PostDeleteService;
import org.denevell.natch.serv.PostSingle.PostSingleService;
import org.denevell.natch.serv.ThreadAdd.ThreadAddService;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;
import org.denevell.natch.utils.ModelResponse;
import org.denevell.natch.utils.Responses;
import org.denevell.natch.utils.UserGetLoggedInService.User;

@Path("thread_frompost")
public class ThreadFromPost {

  @Context HttpServletRequest mRequest;
  @Inject ThreadFromPostService mThreadFromPostService;

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addThreadFromPost(@Valid AddFromPostInput input) throws IOException {
    User user = (User) mRequest.getAttribute("user");
    return mThreadFromPostService.threadFromPost(input.postId, input.subject, user.admin);
  }

  public static interface ThreadFromPostService {
    Response threadFromPost(long postId, String subject, boolean admin);
  }

  public static class ThreadFromPostServiceImpl implements ThreadFromPostService {
    @Inject PostSingleService mPostSingle;
    @Inject ThreadAddService mThreadAdd;
    @Inject PostDeleteService mPostDelete;

    public Response threadFromPost(long postId, String subject, boolean admin) {
      if(!admin) {
        return new ModelResponse<>(403, null).httpReturn();
      }
      
      PostEntity post = Jrappy2.findObject(JPAFactoryContextListener.sFactory, postId, false, PostEntity.class);

      PostEntity newPost = new PostEntity(post);
      newPost.modified = new Date().getTime();
      newPost.id = 0;
      newPost.adminEdited = true;
      newPost.threadId = PostEntity.Utils.createNewThreadId(null, subject);
      newPost.subject = subject;
      ThreadEntity threadEntity = new ThreadEntity(newPost, Arrays.asList(newPost));
      threadEntity.id = newPost.threadId;
      threadEntity.numPosts = 1;

      Response addResult = mThreadAdd.threadAdd(threadEntity);
      if(addResult.getStatus()!=200) {
        return new ModelResponse<Void>(addResult.getStatus(), null).httpReturn();
      }

      Response deleteResult = mPostDelete.delete(post.threadId, postId, post.username, true);
      if(deleteResult.getStatus()!=200) {
        return new ModelResponse<Void>(deleteResult.getStatus(), null).httpReturn();
      }

      // Hash of user id
      return new ModelResponse<>(200, Responses.hM("threadId", threadEntity.id)).httpReturn();
    }
  }

}

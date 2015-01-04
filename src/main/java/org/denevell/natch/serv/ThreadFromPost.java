package org.denevell.natch.serv;

import java.util.Arrays;
import java.util.Date;

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
import org.denevell.natch.gen.ServDelete;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;
import org.denevell.natch.utils.ModelResponse;
import org.denevell.natch.utils.Responses;
import org.denevell.natch.utils.UserGetLoggedInService.User;

@Path("thread_frompost")
public class ThreadFromPost {

  @Context HttpServletRequest mRequest;

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addThreadFromPost(@Valid AddFromPostInput input) throws Exception {
    User user = (User) mRequest.getAttribute("user");
    return new ThreadFromPostServiceImpl().threadFromPost(input.postId, input.subject, user.admin);
  }

  public static interface ThreadFromPostService {
    Response threadFromPost(long postId, String subject, boolean admin) throws Exception;
  }

  public class ThreadFromPostServiceImpl implements ThreadFromPostService {

    public Response threadFromPost(long postId, String subject, boolean admin) throws Exception {
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

      Response addResult = Jrappy2.persist(JPAFactoryContextListener.sFactory, threadEntity);
      if(addResult.getStatus()!=200) {
        return new ModelResponse<Void>(addResult.getStatus(), null).httpReturn();
      }

      ServDelete servDelete = new ServDelete();
      servDelete.mRequest = mRequest; 
      Response deleteResult = servDelete.delete("PostEntity", null, postId, null);
      if(deleteResult.getStatus()!=200) {
        return new ModelResponse<Void>(deleteResult.getStatus(), null).httpReturn();
      }

      // Hash of user id
      return new ModelResponse<>(200, Responses.hM("threadId", threadEntity.id)).httpReturn();
    }
  }

}

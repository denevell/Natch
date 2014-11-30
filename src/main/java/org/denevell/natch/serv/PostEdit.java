package org.denevell.natch.serv;

import java.util.Date;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.PostEntity.EditInput;
import org.denevell.natch.model.UserGetLoggedInModel.User;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("post_edit/{postId}")
public class PostEdit {

  @Context HttpServletRequest mRequest;
  @Inject PostEditService mPostEditService;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response editpost(
      @PathParam(value = "postId") long postId, 
      @Valid EditInput input) {
    User user = (User) mRequest.getAttribute("user");
    return mPostEditService.edit(postId, input.adapt(), user.username, user.admin);
  }


  public static interface PostEditService {
    default Response edit(long postId, PostEntity editedPostEntity, String username, boolean admin) {
      return Jrappy2.update( 
          JPAFactoryContextListener.sFactory, 
          PostEntity.class, 
          postId, 
          (postEntity) -> { // Do we have access rights
            return admin || postEntity.username.equals(username);
          },
          (postEntity) -> { // Edit thread
            postEntity.content = editedPostEntity.content;
            if (editedPostEntity.subject != null) postEntity.subject = editedPostEntity.subject;
            if (editedPostEntity.tags != null) postEntity.tags = editedPostEntity.tags;
            postEntity.modified = new Date().getTime();
            if (!username.equals(postEntity.username) && admin) postEntity.adminEdited = true;
            return postEntity;
          });
    }
  }

}

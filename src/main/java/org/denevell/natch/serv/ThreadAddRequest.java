package org.denevell.natch.serv;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.PostAddModel;
import org.denevell.natch.model.PostEntity;
import org.denevell.natch.model.PushEntity;
import org.denevell.natch.model.ThreadEntity;
import org.denevell.natch.model.UserGetLoggedInModel;
import org.denevell.natch.model.UserGetLoggedInModel.User;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.ManifestVars;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

@Path("post/addthread")
public class ThreadAddRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostAddModel mAddPostModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	
	// TODO: Return the thread id?
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addThread(@Valid AddThreadResourceInput input) {
		User userEntity = (User) mRequest.getAttribute("user");
		if (input.tags!=null && !PostEntity.isTagLengthOkay(input.tags)) {
			return Response.status(400).build(); // How to know a 400 means that?
		} else if (PostEntity.isSubjectTooLarge(input.subject)) {
			return Response.status(400).build(); // How to know a 400 means that?
		} else {
		  PostEntity post = AddPostRequestToPostEntity.adapt(input, false, userEntity.username);
		  ThreadEntity thread = mAddPostModel.add(post);
		  if(thread==null) {
		    return Response.serverError().build();
		  }
		  sendPushNotifications(thread);
		  return Response.ok().build();
		}
	}	

  public static class AddThreadResourceInput {
    @NotEmpty @NotBlank 
    public String subject;
    @NotEmpty @NotBlank 
    public String content;
    @NotEmpty @NotBlank 
    public String threadId;
    public List<String> tags;
  }

  public static class AddPostRequestToPostEntity {

    public static PostEntity adapt(AddThreadResourceInput input, boolean adminEdited, String username) {
      PostEntity pe = new PostEntity();
      long created = new Date().getTime();
      pe.content = (input.content);
      pe.subject = (input.subject);
      pe.threadId = (input.threadId);
      pe.tags = (input.tags);
      pe.username = (username);
      pe.created = (created);
      pe.modified = (created);
      if (adminEdited) {
        pe.adminEdited = true;
      }
      return pe;
    }
  }

  private void sendPushNotifications(final ThreadEntity thread) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        String key = ManifestVars.getGCMKey();
        if (key == null || key.trim().length() == 0) {
          Log.error(getClass(), "GCM KEY is null or blank");
        }
        Sender sender = new Sender(key);
        Jrappy<PushEntity> jrappy = new Jrappy<PushEntity>(
            JPAFactoryContextListener.sFactory);
        List<PushEntity> list = null;
        list = jrappy
              .namedQuery(PushEntity.NAMED_QUERY_LIST_IDS)
              .list(PushEntity.class);
        if(list!=null) {
          for (PushEntity pushEntity : list) {
            try {
              String registrationId = pushEntity.getClientId();
              String s = new ObjectMapper() .writeValueAsString(new CutDownThreadResource(thread));
              Message message = new Message.Builder().addData("thread", s) .build();
              Result result = sender.send(message, registrationId, 5);
              Log.info(ThreadAddRequest.class, "Push send result: " + result);
            } catch (Exception e) {
              Log.info(ThreadAddRequest.class, "Error sending push message: "
                  + e.getMessage());
              e.printStackTrace();
            }
          }
          
        }
      }
    }).start();
  }

  public static class CutDownThreadResource {

    public List<String> tags;
    public String id;
    public String subject;
    public String author;
    public long numPosts;
    public long creation;
    public long modification;
    public long rootPostId;

    public CutDownThreadResource() {}
    
    public CutDownThreadResource(ThreadEntity tr) {
      subject = tr.getRootPost().getSubject();
      author = tr.getRootPost().username;
      numPosts = tr.getNumPosts();
      tags = tr.getRootPost().getTags();
      rootPostId = tr.getRootPost().id;
      modification = tr.getRootPost().modified;
      creation = tr.getRootPost().created;
      id = tr.getId();
    }

  }

}

package org.denevell.natch.serv.thread;

import java.util.List;
import java.util.ResourceBundle;

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
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.denevell.jrappy.Jrappy;
import org.denevell.natch.adapters.AddPostRequestToPostEntity;
import org.denevell.natch.adapters.ThreadEntityToThreadResource;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.CutDownThreadResource;
import org.denevell.natch.io.users.User;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.PushEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.PostAddModel;
import org.denevell.natch.model.interfaces.UserGetLoggedInModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.ManifestVars;
import org.denevell.natch.utils.Strings;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

@Path("post/addthread")
public class AddThreadRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostAddModel mAddPostModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public AddThreadRequest() {
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData addThread(@Valid AddPostResourceInput input) {
		User userEntity = (User) mRequest.getAttribute("user");
		if (input.getTags()!=null && !PostEntity.isTagLengthOkay(input.getTags())) {
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.tag_too_large));
			return regReturnData;
		} else if (PostEntity.isSubjectTooLarge(input.getSubject())) {
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.subject_too_large));
			return regReturnData;
		} else {
			return addPostOrThread(input, userEntity.getUsername());
		}
	}	

    public AddPostResourceReturnData addPostOrThread(AddPostResourceInput input, String username) {
		AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
		regReturnData.setSuccessful(false);
	    final PostEntity post = AddPostRequestToPostEntity.adapt(input, false, username);
	    ThreadEntity thread = mAddPostModel.add(post);
		generateAddPostReturnResource(regReturnData, thread);
		sendPushNotifications(regReturnData);
		return regReturnData;
	}

	private void sendPushNotifications(final AddPostResourceReturnData thread) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String key = ManifestVars.getGCMKey();
				if(key==null || key.trim().length()==0) {
					Log.error(getClass(), "GCM KEY is null or blank");
				}
				Sender sender = new Sender(key);
				List<PushEntity> list = new Jrappy<PushEntity>(JPAFactoryContextListener.sFactory)
						.startTransaction()
						.namedQuery(PushEntity.NAMED_QUERY_LIST_IDS)
						.list(PushEntity.class);
				for (PushEntity pushEntity : list) {
					try {
						String registrationId = pushEntity.getClientId();
						String s = new ObjectMapper().writeValueAsString(new CutDownThreadResource(thread.getThread()));
						Message message = new Message.Builder().addData("thread", s).build();
						Result result = sender.send(message, registrationId, 5);
						Log.info(AddThreadRequest.class, "Push send result: " + result);
					} catch (Exception e) {
						Log.info(AddThreadRequest.class, "Error sending push message: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void generateAddPostReturnResource(AddPostResourceReturnData regReturnData, ThreadEntity thread) {
		if(thread!=null) {
				regReturnData.setThread(ThreadEntityToThreadResource.adapt(thread));
				regReturnData.setSuccessful(true);
		} else {
			Log.info(getClass(), "Added a post but the thread id was null when sending the json response...");
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		}
	}

}

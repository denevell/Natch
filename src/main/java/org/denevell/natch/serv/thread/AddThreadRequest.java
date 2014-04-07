package org.denevell.natch.serv.thread;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.adapters.AddPostRequestToPostEntity;
import org.denevell.natch.db.adapters.ThreadEntityToThreadResource;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.PushEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.CutDownThreadResource;
import org.denevell.natch.serv.post.ThreadFactory;
import org.denevell.natch.serv.post.edit.EditPostModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.ManifestUtils;
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
	private ResourceBundle rb = Strings.getMainResourceBundle();
	private ThreadFactory mThreadFactory;
	private CallDbBuilder<ThreadEntity> mModel = new CallDbBuilder<ThreadEntity>();
	
	public AddThreadRequest() {
		mThreadFactory = new ThreadFactory();
	}
	
	/**
	 * For DI testing.
	 */
	public AddThreadRequest(
			HttpServletRequest request, 
			HttpServletResponse response
			) {
		mRequest = request;
		mResponse = response;
	}
		

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData addThread(AddPostResourceInput input) {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if(EditPostModel.isBadInputParams(userEntity, 
				input.getSubject(), 
				input.getContent(), true)) {
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
			return regReturnData;
		} else if (input.getTags()!=null && !PostEntity.isTagLengthOkay(input.getTags())) {
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
			return addPostOrThread(input, userEntity);
		}
	}	

    public AddPostResourceReturnData addPostOrThread(AddPostResourceInput input, UserEntity userEntity) {
		AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
		regReturnData.setSuccessful(false);
	    final PostEntity post = AddPostRequestToPostEntity.adapt(input, false, userEntity);
		ThreadEntity thread = mModel
			.startTransaction()
			.createOrUpdate(
				post.getThreadId(),
				new CallDbBuilder.UpdateItem<ThreadEntity>() {
					@Override public ThreadEntity update(ThreadEntity item) {
						return mThreadFactory.makeThread(item, post);
					}
				}, new CallDbBuilder.NewItem<ThreadEntity>() {
					@Override public ThreadEntity newItem() {
						return mThreadFactory.makeThread(post);
					}
				}, 
				ThreadEntity.class);		
		mModel.commitAndCloseEntityManager();
		generateAddPostReturnResource(regReturnData, thread);
		sendPushNotifications(regReturnData);
		return regReturnData;
	}

	private void sendPushNotifications(final AddPostResourceReturnData thread) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String key = null;
				try {
					key = ManifestUtils.getManifest(context).getValue("GCM_KEY");
					if (key != null && !key.trim().isEmpty()) {
					} else {
						Log.error(AddThreadRequest.class, "GCM key looks bad -- empty");
					}
				} catch (IOException e1) {
					Log.error(AddThreadRequest.class, "GCM key looks bad -- empty");
					e1.printStackTrace();
				}
				Sender sender = new Sender(key);
				List<PushEntity> list = new CallDbBuilder<PushEntity>()
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

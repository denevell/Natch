package org.denevell.natch.serv.thread.add;

import java.util.ResourceBundle;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

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
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.serv.post.add.AddPostModel;
import org.denevell.natch.serv.post.add.AddPostRequest;
import org.denevell.natch.serv.post.edit.EditPostModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("post/addthread")
public class AddThreadRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private AddPostModel mModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public AddThreadRequest() {
		mModel = new AddPostModel();
	}
	
	/**
	 * For DI testing.
	 */
	public AddThreadRequest(AddPostModel postModel, 
			HttpServletRequest request, 
			HttpServletResponse response
			) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}
		

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Add a post",	notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.posts.resources.AddPostResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})	
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
		try {
			mModel.init();
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
			ThreadEntity thread = mModel.addPost(userEntity, input);
			generateAddPostReturnResource(regReturnData, thread);
			sendPushNotifications(regReturnData);
			return regReturnData;
		} finally {
			mModel.close();
		}
	}

	private void sendPushNotifications(final AddPostResourceReturnData thread) {
		new Thread(new Runnable() {
			@Override
			public void run() {	
		       String id = "APA91bHxWB0Ss1Xv_22Y7Vxa4Qt4Ppi2fqLi532t5tBk0MI6uVLKClVTE9TP4qpcSjznUOPw3byMn2sJnVP8JPcrruWtg3FJ5X-25dKIS5FS-cz9jfvMmgy28d_ECfaEVw0uq3MRAp5X-g_grzfZux-5G-Kqph6qIQCbCHiMQ7jE8ckQcwCeslQ";
		       String key = "AIzaSyDa1_2hWr2uH7VTEUf95rN7uev3Z5AJGi0";
		       Sender sender = new Sender(key);
		       String registrationId = id;
		       try {
		    	   String s = new ObjectMapper().writeValueAsString(thread);
		    	   Message message = new Message.Builder().addData("thread", s).build();
		    	   Result result = sender.send(message, registrationId, 5);
		    	   Log.info(AddThreadRequest.class, "Push send result: " + result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
		}).start();
	}

	private void generateAddPostReturnResource(AddPostResourceReturnData regReturnData, ThreadEntity thread) {
		if(thread!=null) {
				regReturnData.setThread(AddPostRequest.adaptThread(thread));
				regReturnData.setSuccessful(true);
		} else {
			Log.info(getClass(), "Added a post but the thread id was null when sending the json response...");
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		}
	}

}

package org.denevell.natch.serv.thread.frompost;

import java.io.IOException;
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

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.AddThreadFromPostResourceInput;
import org.denevell.natch.serv.post.add.AddPostModel;
import org.denevell.natch.serv.post.add.AddPostRequest;
import org.denevell.natch.serv.post.delete.DeletePostModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

@Path("thread/frompost")
public class ThreadFromPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private AddPostModel mModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
    private DeletePostModel mDeletePostModel;
	
	public ThreadFromPostRequest() {
		mModel = new AddPostModel();
		mDeletePostModel = new DeletePostModel();
	}
	
	/**
	 * For DI testing.
	 */
	public ThreadFromPostRequest(AddPostModel postModel, 
	        DeletePostModel deletePostModel,
			HttpServletRequest request, 
			HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
		mDeletePostModel = deletePostModel;
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData addThreadFromPost(AddThreadFromPostResourceInput input) throws IOException {
		try {
			mModel.init();
			mDeletePostModel.init();
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
		    UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		    if(!userEntity.isAdmin()) {
		        mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		        return null;
		    }
			ThreadEntity thread = mModel.addPostAsDifferntUser(input.getUserId(), input);
			mDeletePostModel.delete(userEntity, input.getPostId());
			generateAddPostReturnResource(regReturnData, thread);
			return regReturnData;
		} finally {
			mModel.close();
			mDeletePostModel.init();
		}
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

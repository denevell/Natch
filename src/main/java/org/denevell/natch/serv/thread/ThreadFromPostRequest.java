package org.denevell.natch.serv.thread;

import java.io.IOException;
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

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.adapters.ThreadEntityToThreadResource;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.AddThreadFromPostResourceInput;
import org.denevell.natch.model.interfaces.ThreadFromPostModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

@Path("thread/frompost")
public class ThreadFromPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject ThreadFromPostModel mThreadFromPostModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public ThreadFromPostRequest() {
	}
	
	/**
	 * For DI testing.
	 */
	public ThreadFromPostRequest(
			HttpServletRequest request, 
			HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData addThreadFromPost(@Valid AddThreadFromPostResourceInput input) throws IOException {
	    UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
	    if(!userEntity.isAdmin()) {
	        mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        return null;
	    }
	    ThreadEntity thread = mThreadFromPostModel.makeNewThread(input.getPostId(), input.getSubject());
		AddPostResourceReturnData returnData = generateAddPostReturnResource(thread);
		return returnData;
	}

	private AddPostResourceReturnData generateAddPostReturnResource(ThreadEntity thread) {
		AddPostResourceReturnData returnData = new AddPostResourceReturnData();
		if(thread!=null) {
			returnData.setThread(ThreadEntityToThreadResource.adapt(thread));
			returnData.setSuccessful(true);
		} else {
			Log.info(getClass(), "Added a post but the thread id was null when sending the json response...");
			returnData.setSuccessful(false);
			returnData.setError(rb.getString(Strings.unknown_error));
		}
		return returnData;
	}
	
}

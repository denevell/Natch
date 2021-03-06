package org.denevell.natch.serv.post;

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

import org.denevell.natch.adapters.AddPostRequestToPostEntity;
import org.denevell.natch.adapters.ThreadEntityToThreadResource;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.io.users.User;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.PostAddModel;
import org.denevell.natch.model.interfaces.UserGetLoggedInModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

@Path("post/add")
public class AddPostRequest {
	
	@Context UriInfo mInfo;
	@Context ServletContext context;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Inject PostAddModel mAddPostModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public AddPostRequest() {
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData addPost(@Valid AddPostResourceInput input) {
		User userEntity = (User) mRequest.getAttribute("user");
		return addPost(input, userEntity.getUsername());
	}
	
	private AddPostResourceReturnData addPost(AddPostResourceInput input, String username) {
		AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
		regReturnData.setSuccessful(false);
	    final PostEntity post = AddPostRequestToPostEntity.adapt(input, false, username);
	    ThreadEntity okay = mAddPostModel.add(post);
		generateAddPostReturnResource(regReturnData, okay, input);
		return regReturnData;
	}	
	
	private void generateAddPostReturnResource(AddPostResourceReturnData regReturnData, ThreadEntity thread, AddPostResourceInput input) {
		if(thread!=null) {
			if(input !=null) {
				ThreadResource threadResource = ThreadEntityToThreadResource.adapt(thread);
				threadResource.setPosts(null);
				regReturnData.setThread(threadResource);
			} else {
				Log.info(getClass(), "Added a post but the thread id was null when sending the json response...");
			}
			regReturnData.setSuccessful(true);
		} else {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		}
	}

}

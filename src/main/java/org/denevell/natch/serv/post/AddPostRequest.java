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

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.adapters.AddPostRequestToPostEntity;
import org.denevell.natch.db.adapters.ThreadEntityToThreadResource;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.model.interfaces.PostAddModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

@Path("post/add")
public class AddPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostAddModel mAddPostModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public AddPostRequest() {
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData addPost(@Valid AddPostResourceInput input) {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		return addPost(input, userEntity);
	}
	
	private AddPostResourceReturnData addPost(AddPostResourceInput input, UserEntity userEntity) {
		AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
		regReturnData.setSuccessful(false);
	    final PostEntity post = AddPostRequestToPostEntity.adapt(input, false, userEntity);
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

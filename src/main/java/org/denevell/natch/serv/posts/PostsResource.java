package org.denevell.natch.serv.posts;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostsModel.AddPostResult;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

@Path("post")
public class PostsResource {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private PostsModel mModel;
	
	public PostsResource() {
		mModel = new PostsModel();
	}
	
	/**
	 * For DI testing.
	 */
	public PostsResource(PostsModel postModel, HttpServletRequest request) {
		mModel = postModel;
		mRequest = request;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData add(AddPostResourceInput input) {
		AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
		if(input==null) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
			return regReturnData;
		}
		UserEntity userEntity = null;
		try {
			userEntity = (UserEntity) mRequest.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER);
		} catch (Exception e) {
			Log.info(getClass(), e.toString());
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error)); // Unknown as his shouldn't happen
			return regReturnData;
		}
		// End of error checking
		AddPostResult okay = mModel.addPost(
				userEntity,
				input.getSubject(), 
				input.getContent());
		if(okay==AddPostResult.ADDED) {
			regReturnData.setSuccessful(true);
		} else if(okay==AddPostResult.BAD_USER_INPUT) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
		} else if(okay==AddPostResult.UNKNOWN_ERROR){
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		} else {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		}
		
		return regReturnData;
	}

}

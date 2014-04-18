package org.denevell.natch.serv.post;

import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.io.posts.DeletePostResourceReturnData;
import org.denevell.natch.io.users.User;
import org.denevell.natch.model.interfaces.PostDeleteModel;
import org.denevell.natch.model.interfaces.UserGetLoggedInModel;
import org.denevell.natch.utils.Strings;

@Path("post/del")
public class DeletePostRequest {
	
	public final static String EDITED = "edited";
	public final static String ADDED = "added";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostDeleteModel mPostFindModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public DeletePostRequest() {
	}
	
	/**
	 * For DI testing.
	 */
	public DeletePostRequest(
			HttpServletRequest request, 
			HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}
	
	@DELETE
	@Path("{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public DeletePostResourceReturnData delete(@PathParam("postId") long number) {
		DeletePostResourceReturnData ret = new DeletePostResourceReturnData();
		ret.setSuccessful(false);
		User userEntity = (User) mRequest.getAttribute("user");
		int result = mPostFindModel.delete(number, userEntity.getUsername(), userEntity.isAdmin());
		generateDeleteReturnResource(result, ret);
		return ret;
	}

	private void generateDeleteReturnResource(int result, DeletePostResourceReturnData ret) {
		if(result == PostDeleteModel.DELETED) {
			ret.setSuccessful(true);
		} else if(result == PostDeleteModel.DOESNT_EXIST) {
			ret.setError(rb.getString(Strings.post_doesnt_exist));
		} else if(result == PostDeleteModel.NOT_YOURS) {
			ret.setError(rb.getString(Strings.post_not_yours));
		} 
	}

}

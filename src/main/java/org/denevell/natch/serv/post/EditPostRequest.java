package org.denevell.natch.serv.post;

import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.PostEditModel;
import org.denevell.natch.model.interfaces.UserGetLoggedInModel;
import org.denevell.natch.serv.thread.EditThreadRequest;
import org.denevell.natch.utils.Strings;

@Path("post/editpost")
public class EditPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostEditModel mPostEditModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public EditPostRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public EditPostRequest(HttpServletRequest request, HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}
	
	@POST
	@Path("{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public EditPostResourceReturnData editpost(
			@PathParam(value="postId") long postId, 
			@Valid EditPostResource editPostResource) {
		EditPostResourceReturnData ret = new EditPostResourceReturnData();
		final UserEntity userEntity = mUserLogggedInModel.get(mRequest);
		PostEntity editData = new PostEntity();
		editData.setContent(editPostResource.getContent());
		editData.setSubject("-");
		int result = mPostEditModel.edit(postId, userEntity, editData);
		EditThreadRequest.generateEditReturnResource(ret, result, rb);
		return ret;
	}

}
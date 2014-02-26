package org.denevell.natch.serv.post.edit;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.serv.thread.EditThreadRequest;
import org.denevell.natch.utils.Strings;

@Path("post/editpost")
public class EditPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	private EditPostModel mModel;
	
	public EditPostRequest() {
		mModel = new EditPostModel();
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public EditPostRequest(EditPostModel postModel, HttpServletRequest request, HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}
	
	@POST
	@Path("{postId}") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	public EditPostResourceReturnData editpost(
			@PathParam(value="postId") long postId, 
			EditPostResource editPostResource) {
		return edit(postId, editPostResource, false);
	}

	private EditPostResourceReturnData edit(long postId, 
			EditPostResource editPostResource, 
			boolean isEditingThread) {
		EditPostResourceReturnData ret = new EditPostResourceReturnData();
		try {
			mModel.init();
			ret.setSuccessful(false);
			UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
			PostEntity mPe = new PostEntity();
			mPe.setContent(editPostResource.getContent());
			String result = mModel.edit(userEntity, postId, mPe, isEditingThread); 
			EditThreadRequest.generateEditReturnResource(ret, result, rb);
			return ret;
		} finally {
			mModel.close();
		} 		
	}
}
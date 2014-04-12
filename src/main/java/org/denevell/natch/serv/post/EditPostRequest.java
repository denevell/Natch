package org.denevell.natch.serv.post;

import java.util.Date;
import java.util.ResourceBundle;

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

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.io.threads.EditThreadResource;
import org.denevell.natch.serv.thread.EditThreadRequest;
import org.denevell.natch.utils.Strings;

@Path("post/editpost")
public class EditPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	private CallDbBuilder<PostEntity> mPostModel;
	
	public EditPostRequest() {
		mPostModel = new CallDbBuilder<PostEntity>();
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
		EditThreadResource r = new EditThreadResource();
		r.setContent(editPostResource.getContent());
		return edit(postId, r, false);
	}

	private EditPostResourceReturnData edit(
			long postId, 
			final EditThreadResource editPostResource, 
			final boolean isEditingThread) {
		EditPostResourceReturnData ret = new EditPostResourceReturnData();
		final UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		mPostModel.startTransaction();
		int result = editPost(postId, editPostResource, isEditingThread, userEntity, mPostModel);
		mPostModel.commitAndCloseEntityManager();
		EditThreadRequest.generateEditReturnResource(ret, result, rb);
		return ret;
	}

	public int editPost(long postId, 
			final EditThreadResource editPostResource,
			final boolean isEditingThread, 
			final UserEntity userEntity, 
			CallDbBuilder<PostEntity> postModel) {
		int result = postModel.updateEntityOnPermission(postId,
				new CallDbBuilder.UpdateItemOnPermissionCorrect<PostEntity>() {
					@Override
					public boolean update(PostEntity item) {
						if(!userEntity.isAdmin() && !item.getUser().getUsername().equals(userEntity.getUsername())) {
							return false;
						}
						item.setContent(editPostResource.getContent());
						if(editPostResource.getSubject()!=null) item.setSubject(editPostResource.getSubject());
						if(editPostResource.getTags()!=null) item.setTags(editPostResource.getTags());
                        item.setModified(new Date().getTime());
                        if(!userEntity.getUsername().equals(item.getUser().getUsername()) && userEntity.isAdmin()) {
                        	item.adminEdited();
                        }
                        if(!isEditingThread) item.setSubject("-");
						return true;
					}
				},
				PostEntity.class);
		return result;
	}
}
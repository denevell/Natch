package org.denevell.natch.serv.thread;

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

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.io.threads.EditThreadResource;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.PostEditModel;
import org.denevell.natch.utils.Strings;

@Path("post/editthread")
public class EditThreadRequest {
	
	public final static String EDITED = "edited";
	public final static String DELETED = "deleted";
	public final static String ADDED = "added";
	public final static String DOESNT_EXIST = "doesntexist";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	public final static String NOT_YOURS_TO_DELETE = "notyourtodelete";
    private static final int TAG_TOO_LARGE = 105;
    private static final int SUBJECT_TOO_LARGE = 106;		
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PostEditModel mPostEditModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public EditThreadRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public EditThreadRequest(HttpServletRequest request, HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}
	
	@POST
	@Path("{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public EditPostResourceReturnData editpost(
			@PathParam(value="postId") long postId, 
			@Valid EditThreadResource editPostResource) {
		return edit(postId, editPostResource, true);
	}

	private EditPostResourceReturnData edit(long postId, 
			EditThreadResource editPostResource, 
			boolean isEditingThread) {
		EditPostResourceReturnData ret = new EditPostResourceReturnData();
		ret.setSuccessful(false);

		if(editPostResource.getTags()!=null && !PostEntity.isTagLengthOkay(editPostResource.getTags())) {
   		    generateEditReturnResource(ret, TAG_TOO_LARGE, rb);
   		    return ret;
		}
		if(editPostResource.getSubject()!=null && PostEntity.isSubjectTooLarge(editPostResource.getSubject())) {
   		    generateEditReturnResource(ret, SUBJECT_TOO_LARGE, rb);
   		    return ret;
		}

		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		PostEntity editingData = new PostEntity();
		editingData.setSubject(editPostResource.getSubject());
		editingData.setContent(editPostResource.getContent());
		editingData.setTags(editPostResource.getTags());
		int result = mPostEditModel.edit(postId, userEntity, editingData);
		generateEditReturnResource(ret, result, rb);
		return ret;
	}
	
	public static void generateEditReturnResource(EditPostResourceReturnData ret,
			int result, ResourceBundle rb) {
		if(result == CallDbBuilder.UPDATED) {
			ret.setSuccessful(true);
		} else if(result == CallDbBuilder.NOT_FOUND) {
			ret.setError(rb.getString(Strings.post_doesnt_exist));
		} else if(result == SUBJECT_TOO_LARGE) {
			ret.setError(rb.getString(Strings.subject_too_large));
		} else if(result == CallDbBuilder.PERMISSION_DENIED) {
			ret.setError(rb.getString(Strings.post_not_yours));
		} else if(result == TAG_TOO_LARGE) {
			ret.setError(rb.getString(Strings.tag_too_large));
		}
	}

}
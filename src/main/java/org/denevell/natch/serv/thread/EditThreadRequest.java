package org.denevell.natch.serv.thread;

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
import org.denevell.natch.serv.post.edit.EditPostModel;
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
    private static final String TAG_TOO_LARGE = "tagtoolarge";
    private static final String SUBJECT_TOO_LARGE = "subtoolarge";		
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	private EditPostModel mModel;
	
	public EditThreadRequest() {
		mModel = new EditPostModel();
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public EditThreadRequest(EditPostModel postModel, HttpServletRequest request, HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}
	
	@POST
	@Path("{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public EditPostResourceReturnData editpost(
			@PathParam(value="postId") long postId, 
			EditPostResource editPostResource) {
		return edit(postId, editPostResource, true);
	}

	private EditPostResourceReturnData edit(long postId, 
			EditPostResource editPostResource, 
			boolean isEditingThread) {
		EditPostResourceReturnData ret = new EditPostResourceReturnData();
		try {
			mModel.init();
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
			PostEntity mPe = new PostEntity();
			mPe.setContent(editPostResource.getContent());
			mPe.setSubject(editPostResource.getSubject());
			mPe.setTags(editPostResource.getTags());
			String result = mModel.edit(userEntity, postId, mPe, isEditingThread); 
			generateEditReturnResource(ret, result, rb);
			return ret;
		} finally {
			mModel.close();
		} 		
	}
	
	public static void generateEditReturnResource(EditPostResourceReturnData ret,
			String result, ResourceBundle rb) {
		if(result.equals(EDITED)) {
			ret.setSuccessful(true);
		} else if(result.equals(DOESNT_EXIST)) {
			ret.setError(rb.getString(Strings.post_doesnt_exist));
		} else if(result.equals(SUBJECT_TOO_LARGE)) {
			ret.setError(rb.getString(Strings.subject_too_large));
		} else if(result.equals(NOT_YOURS_TO_DELETE)) {
			ret.setError(rb.getString(Strings.post_not_yours));
		} else if(result.equals(UNKNOWN_ERROR)) {
			ret.setError(rb.getString(Strings.unknown_error));
		} else if(result.equals(BAD_USER_INPUT)) {
			ret.setError(rb.getString(Strings.post_fields_cannot_be_blank));
		} else if(result.equals(TAG_TOO_LARGE)) {
			ret.setError(rb.getString(Strings.tag_too_large));
		}
	}

}
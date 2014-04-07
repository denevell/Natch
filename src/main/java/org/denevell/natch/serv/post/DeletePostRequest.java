package org.denevell.natch.serv.post;

import java.util.ResourceBundle;

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

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.CallDbBuilder.DeleteOrMerge;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.DeletePostResourceReturnData;
import org.denevell.natch.utils.Strings;

@Path("post/del")
public class DeletePostRequest {
	
	public final static String EDITED = "edited";
	public final static String DELETED = "deleted";
	public final static String ADDED = "added";
	public final static String DOESNT_EXIST = "doesntexist";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	public final static String NOT_YOURS_TO_DELETE = "notyourtodelete";	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	private CallDbBuilder<ThreadEntity> mThreadModel = new CallDbBuilder<ThreadEntity>();
	private CallDbBuilder<PostEntity> mPostModel = new CallDbBuilder<PostEntity>();
	
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
	
	
	public String delete(UserEntity userEntity, long postEntityId) {
		final PostEntity pe = mPostModel.startTransaction().find(postEntityId, false, PostEntity.class);
		if(pe==null) {
			mPostModel.commitAndCloseEntityManager();
			return DOESNT_EXIST;
		} else if(!userEntity.isAdmin() && !pe.getUser().getUsername().equals(userEntity.getUsername())) {
			mPostModel.commitAndCloseEntityManager();
			return NOT_YOURS_TO_DELETE;
		}
		mThreadModel.useTransaction(mPostModel.getEntityManager())
			.findAndUpdateOrDelete(pe.getThreadId(), 
				new DeleteOrMerge<ThreadEntity>() {
					@Override public boolean shouldDelete(ThreadEntity item) {
						item.updateThreadToRemovePost(pe);
						return item.getPosts()==null || item.getPosts().size()==0;
					}
				}, 
				ThreadEntity.class);
		mPostModel.getEntityManager().remove(pe);
		mThreadModel.commitAndCloseEntityManager();
		return DELETED;
	}	
		
	@DELETE
	@Path("{postId}") 
	@Produces(MediaType.APPLICATION_JSON)
	public DeletePostResourceReturnData delete(@PathParam("postId") long number) {
		DeletePostResourceReturnData ret = new DeletePostResourceReturnData();
		ret.setSuccessful(false);
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		String result = delete(userEntity, number);
		generateDeleteReturnResource(result, ret, userEntity);
		return ret;
	}

	private void generateDeleteReturnResource(String result, DeletePostResourceReturnData ret, UserEntity userEntity) {
		if(result.equals(DELETED)) {
			ret.setSuccessful(true);
		} else if(result.equals(DOESNT_EXIST)) {
			ret.setError(rb.getString(Strings.post_doesnt_exist));
		} else if(result.equals(NOT_YOURS_TO_DELETE)) {
			ret.setError(rb.getString(Strings.post_not_yours));
		} 
	}

}

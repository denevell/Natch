package org.denevell.natch.serv.thread;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.adapters.AddPostRequestToPostEntity;
import org.denevell.natch.db.adapters.ThreadEntityToThreadResource;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.AddThreadFromPostResourceInput;
import org.denevell.natch.serv.post.DeletePostRequest;
import org.denevell.natch.serv.post.ThreadFactory;
import org.denevell.natch.serv.post.edit.EditPostModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

@Path("thread/frompost")
public class ThreadFromPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	private ThreadFactory mThreadFactory;
	private CallDbBuilder<UserEntity> mUserModel;
	private CallDbBuilder<ThreadEntity> mModel = new CallDbBuilder<ThreadEntity>();
	
	public ThreadFromPostRequest() {
		mThreadFactory = new ThreadFactory();
		mUserModel = new CallDbBuilder<UserEntity>()
		 .namedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME);
	}
	
	/**
	 * For DI testing.
	 */
	public ThreadFromPostRequest(
			HttpServletRequest request, 
			HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData addThreadFromPost(AddThreadFromPostResourceInput input) throws IOException {
		AddPostResourceReturnData regReturnData = null;
		regReturnData = new AddPostResourceReturnData();
		regReturnData.setSuccessful(false);
	    UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
	    if(!userEntity.isAdmin()) {
	        mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        return null;
	    }
		if(isBadRequest(input, userEntity)) {
		    mResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
	        return null;
	    }
	    UserEntity user = mUserModel .startTransaction().queryParam("username", input.getUserId()).single(UserEntity.class);  	    
	    final PostEntity post = AddPostRequestToPostEntity.adapt(input, true, user);
		ThreadEntity thread = mModel.startTransaction()
			.createOrUpdate(post.getThreadId(),
				new CallDbBuilder.UpdateItem<ThreadEntity>() {
					@Override public ThreadEntity update(ThreadEntity item) {
						return mThreadFactory.makeThread(item, post);
					}
				}, new CallDbBuilder.NewItem<ThreadEntity>() {
					@Override public ThreadEntity newItem() {
						return mThreadFactory.makeThread(post);
					}
				}, 
				ThreadEntity.class);		
		mModel.commitAndCloseEntityManager();
		generateAddPostReturnResource(regReturnData, thread);

		userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		new DeletePostRequest().delete(userEntity, input.getPostId());
		return regReturnData;
	}

	private void generateAddPostReturnResource(AddPostResourceReturnData regReturnData, ThreadEntity thread) {
		if(thread!=null) {
			regReturnData.setThread(ThreadEntityToThreadResource.adapt(thread));
			regReturnData.setSuccessful(true);
		} else {
			Log.info(getClass(), "Added a post but the thread id was null when sending the json response...");
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		}
	}
	
	private boolean isBadRequest(AddThreadFromPostResourceInput input, UserEntity userEntity) {
		return input.getPostId()<=0 
	               || input.getUserId()==null 
	               || input.getUserId().trim().isEmpty()
	               || EditPostModel.isBadInputParams(userEntity, 
	                       input.getSubject(), 
	                       input.getContent(), 
	                       true);
	}

}

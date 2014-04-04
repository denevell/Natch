package org.denevell.natch.serv.post;

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
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.serv.post.edit.EditPostModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

@Path("post/add")
public class AddPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	private ThreadFactory mThreadFactory;
	
	public AddPostRequest() {
		mThreadFactory = new ThreadFactory();
	}
	
	/**
	 * For DI testing.
	 */
	public AddPostRequest(
			HttpServletRequest request, 
			HttpServletResponse response) {
		mRequest = request;
		mResponse = response;
	}
		
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData addPost(AddPostResourceInput input) {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if(EditPostModel.isBadInputParams(userEntity, 
				input.getSubject(), 
				input.getContent(), false)) {
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
			return regReturnData;
		} else {
			return addPost(input, userEntity);
		}
	}
	
	private AddPostResourceReturnData addPost(AddPostResourceInput input, UserEntity userEntity) {
		AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
		regReturnData.setSuccessful(false);
	    final PostEntity post = AddPostRequestToPostEntity.adapt(input, false, userEntity);
		ThreadEntity okay = new CallDbBuilder<ThreadEntity>().createOrUpdate(
				post.getThreadId(),
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

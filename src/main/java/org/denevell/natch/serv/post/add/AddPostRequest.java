package org.denevell.natch.serv.post.add;

import java.util.ArrayList;
import java.util.List;
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
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.serv.post.edit.EditPostModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("post/add")
public class AddPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private AddPostModel mModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public AddPostRequest() {
		mModel = new AddPostModel();
	}
	
	/**
	 * For DI testing.
	 */
	public AddPostRequest(AddPostModel postModel, 
			HttpServletRequest request, 
			HttpServletResponse response
			) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}
		
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Add a post",	notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.posts.resources.AddPostResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})	
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
		try {
			mModel.init();
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
			ThreadEntity okay = mModel.addPost(userEntity, input);
			generateAddPostReturnResource(regReturnData, okay, input);
			return regReturnData;
		} finally {
			mModel.close();
		}
	}	
	
	private void generateAddPostReturnResource(AddPostResourceReturnData regReturnData, ThreadEntity thread, AddPostResourceInput input) {
		if(thread!=null) {
			if(input !=null) {
				ThreadResource threadResource = adaptThread(thread);
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

	public static ThreadResource adaptThread(ThreadEntity thread) {
		ThreadResource tr = new ThreadResource();
		List<PostResource> postsResources = new ArrayList<PostResource>();
		for (PostEntity p: thread.getPosts()) {
			PostResource postResource = new PostResource(p.getUser().getUsername(), 
					p.getCreated(), 
					p.getModified(), 
					p.getSubject(), 
					p.getContent(),
					p.getTags(), 
					p.isAdminEdited());
			postResource.setId(p.getId());
			postResource.setThreadId(p.getThreadId());
			postsResources.add(postResource);
		}
		if(postsResources.size()>0) {
			tr.setSubject(postsResources.get(0).getSubject());
			tr.setAuthor(thread.getRootPost().getUser().getUsername());
		}
		tr.setPosts(postsResources);
		tr.setNumPosts((int) thread.getNumPosts());
		tr.setId(thread.getId());
		return tr;
	}	
	
}

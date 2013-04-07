package org.denevell.natch.serv.posts;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.PostsModel.AddPostResult;
import org.denevell.natch.serv.posts.PostsModel.DeletePostResult;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

@Path("post")
public class PostsResource {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private PostsModel mModel;
	
	public PostsResource() {
		mModel = new PostsModel();
	}
	
	/**
	 * For DI testing.
	 */
	public PostsResource(PostsModel postModel, HttpServletRequest request, HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddPostResourceReturnData add(AddPostResourceInput input) {
		AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
		if(input==null) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
			return regReturnData;
		}
		UserEntity userEntity = null;
		try {
			userEntity = (UserEntity) mRequest.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER);
		} catch (Exception e) {
			Log.info(getClass(), e.toString());
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error)); // Unknown as this shouldn't happen
			return regReturnData;
		}
		// End of error checking
		AddPostResult okay = mModel.addPost(
				userEntity,
				input.getSubject(), 
				input.getContent());
		if(okay==AddPostResult.ADDED) {
			regReturnData.setSuccessful(true);
		} else if(okay==AddPostResult.BAD_USER_INPUT) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
		} else if(okay==AddPostResult.UNKNOWN_ERROR){
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		} else {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		}
		
		return regReturnData;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ListPostsResource listByModificationDate() throws IOException {
		List<PostEntity> posts = null;
		try {
			posts = mModel.listByModificationDate();
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		}
		if(posts==null) {
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} else {
			ListPostsResource adaptedPosts = new ListPostsResourceAdapter(posts);
			return adaptedPosts;
		}
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public DeletePostResource delete(long number) {
		DeletePostResource ret = new DeletePostResource();
		ret.setSuccessful(false);
		UserEntity userEntity = null;
		try {
			userEntity = (UserEntity) mRequest.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER);
		} catch (Exception e) {
			Log.info(getClass(), e.toString());
			ret.setSuccessful(false);
			ret.setError(rb.getString(Strings.unknown_error)); // Unknown as this shouldn't happen
			return ret;
		}
		try {
			DeletePostResult result = mModel.delete(userEntity, number);
			if(result==DeletePostResult.DELETED) {
				ret.setSuccessful(true);
			} else if(result==DeletePostResult.DOESNT_EXIST) {
				ret.setError(rb.getString(Strings.post_doesnt_exist));
			} else if(result==DeletePostResult.NOT_YOURS_TO_DELETE) {
				ret.setError(rb.getString(Strings.post_not_yours_to_delete));
			} else if(result==DeletePostResult.UNKNOWN_ERROR) {
				ret.setError(rb.getString(Strings.unknown_error));
			}
			return ret;
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't delete post: " + e.toString());
			ret.setError(rb.getString(Strings.unknown_error));
			return ret;
		} 
	}

}

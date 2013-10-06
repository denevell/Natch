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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import org.denevell.natch.io.posts.DeletePostResourceReturnData;
import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("post")
@Api(value="/post", description="Adds, Deletes, Edits and Lists posts")
public class PostsREST {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	private PostsModel mModel;
	private EditPostResourcePostEntityAdapter mEditPostAdapter;
	private AddPostResourcePostEntityAdapter mAddPostAdapter;
	
	public PostsREST() {
		mModel = new PostsModel();
		mEditPostAdapter = new EditPostResourcePostEntityAdapter();
		mAddPostAdapter = new AddPostResourcePostEntityAdapter();
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public PostsREST(PostsModel postModel, HttpServletRequest request, HttpServletResponse response, EditPostResourcePostEntityAdapter editPostAdapter, AddPostResourcePostEntityAdapter addPostAdapter) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
		mEditPostAdapter = editPostAdapter;
		mAddPostAdapter = addPostAdapter;
	}

	
	@PUT
	@Path("/addthread") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Add a post",	notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.posts.resources.AddPostResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})	
	public AddPostResourceReturnData addThread(AddPostResourceInput input) {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if(PostsModel.isBadInputParams(userEntity, 
				input.getSubject(), 
				input.getContent(), true)) {
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
			return regReturnData;
		} else {
			return addPostOrThread(input, userEntity);
		}
	}	

	public AddPostResourceReturnData addPostOrThread(AddPostResourceInput input, UserEntity userEntity) {
		try {
			mModel.init();
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
			mAddPostAdapter.create(input);
			ThreadEntity thread = mModel.addPost(userEntity, mAddPostAdapter);
			generateAddPostReturnResource(regReturnData, thread, mAddPostAdapter);
			return regReturnData;
		} finally {
			mModel.close();
		}
	}

	private void generateAddPostReturnResource(AddPostResourceReturnData regReturnData, ThreadEntity thread, AddPostResourcePostEntityAdapter adapterThatCreatePost) {
		if(thread!=null) {
			if(adapterThatCreatePost!=null && adapterThatCreatePost.getCreatedPost()!=null) {
				regReturnData.setThread(new ThreadResourceAdapter(thread));
			} else {
				Log.info(getClass(), "Added a post but the thread id was null when sending the json response...");
			}
			regReturnData.setSuccessful(true);
		} else {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		}
	}
	
	@GET
	@Path("/{postId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get post by id", responseClass="org.denevell.natch.serv.posts.resources.PostResource")
	public PostResource findById(
		@ApiParam(name="postId") @PathParam("postId") long postId 
			) throws IOException {
		PostEntity post = null;
		try {
			mModel.init();
			post = mModel.findPostById(postId);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't find post: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} finally {
			mModel.close();
		}
		if(post==null) {
			mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} else {
			PostResource postResource = new PostResource(post.getUser().getUsername(), 
					post.getCreated(), 
					post.getModified(), 
					post.getSubject(), 
					post.getContent(), 
					post.getTags());
			postResource.setId(post.getId());
			postResource.setThreadId(post.getThreadId());
			return postResource;
		}
	}	

	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Lists posts by modification date", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ListPostsResource listByModificationDate(
		@ApiParam(name="start") @PathParam("start") int start, 	
		@ApiParam(name="limit") @PathParam("limit") int limit 	
			) throws IOException {
		List<PostEntity> posts = null;
		try {
			mModel.init();
			posts = mModel.listByModificationDate(start, limit);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} finally {
			mModel.close();
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
	@Path("/del/{postId}") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete a post", 	notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.posts.resources.DeletePostResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})	
	public DeletePostResourceReturnData delete(
			@ApiParam(name="postId") @PathParam("postId") long number) {
		DeletePostResourceReturnData ret = new DeletePostResourceReturnData();
		ret.setSuccessful(false);
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		try {
			mModel.init();
			if(userEntity==null) {
				ret.setError(rb.getString(Strings.unknown_error)); // Unknown as this shouldn't happen
				return ret;
			}	
			String result = mModel.delete(userEntity, number);
			generateDeleteReturnResource(result, ret, userEntity);
			return ret;
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't delete post: " + e.toString());
			ret.setError(rb.getString(Strings.unknown_error));
			return ret;
		} finally {
			mModel.close();
		} 
	}

	private void generateDeleteReturnResource(String result, DeletePostResourceReturnData ret, UserEntity userEntity) {
		if(result.equals(PostsModel.DELETED)) {
			ret.setSuccessful(true);
		} else if(result.equals(PostsModel.DOESNT_EXIST)) {
			ret.setError(rb.getString(Strings.post_doesnt_exist));
		} else if(result.equals(PostsModel.NOT_YOURS_TO_DELETE)) {
			ret.setError(rb.getString(Strings.post_not_yours));
		} else if(result.equals(PostsModel.UNKNOWN_ERROR)) {
			ret.setError(rb.getString(Strings.unknown_error));
		}
	}

	@POST
	@Path("/editpost/{postId}") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Edit a post", notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.posts.resources.EditPostResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})	
	public EditPostResourceReturnData editpost(
			@ApiParam(name="postId") @PathParam(value="postId") long postId, 
			@ApiParam(name="editParam") EditPostResource editPostResource) {
		return edit(postId, editPostResource, false);
	}
	
	@POST
	@Path("/editthread/{postId}") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Edit a thread", notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.posts.resources.EditPostResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})	
	public EditPostResourceReturnData editthread(
			@ApiParam(name="postId") @PathParam(value="postId") long postId, 
			@ApiParam(name="editParam") EditPostResource editPostResource) {
		return edit(postId, editPostResource, true);
	}		

	private EditPostResourceReturnData edit(long postId, 
			EditPostResource editPostResource, 
			boolean isEditingThread) {
		EditPostResourceReturnData ret = new EditPostResourceReturnData();
		try {
			mModel.init();
			ret.setSuccessful(false);
			UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
			mEditPostAdapter.setPostWithNewData(editPostResource);
			String result = mModel.edit(userEntity, postId, mEditPostAdapter, isEditingThread); 
			generateEditReturnResource(ret, result);
			return ret;
		} finally {
			mModel.close();
		} 		
	}
	
	private void generateEditReturnResource(EditPostResourceReturnData ret,
			String result) {
		if(result.equals(PostsModel.EDITED)) {
			ret.setSuccessful(true);
		} else if(result.equals(PostsModel.DOESNT_EXIST)) {
			ret.setError(rb.getString(Strings.post_doesnt_exist));
		} else if(result.equals(PostsModel.NOT_YOURS_TO_DELETE)) {
			ret.setError(rb.getString(Strings.post_not_yours));
		} else if(result.equals(PostsModel.UNKNOWN_ERROR)) {
			ret.setError(rb.getString(Strings.unknown_error));
		} else if(result.equals(PostsModel.BAD_USER_INPUT)) {
			ret.setError(rb.getString(Strings.post_fields_cannot_be_blank));
		}
	}

}

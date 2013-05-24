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
	@Path("/add") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Add a post",	notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.posts.resources.AddPostResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})	
	public AddPostResourceReturnData add(
			@ApiParam(name="input") AddPostResourceInput input) {
		try {
			mModel.init();
			AddPostResourceReturnData regReturnData = new AddPostResourceReturnData();
			regReturnData.setSuccessful(false);
			if(input==null || input.getContent()==null || input.getSubject()==null) {
				regReturnData.setSuccessful(false);
				regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
				return regReturnData;
			}
			UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
			if(userEntity==null) {
				regReturnData.setError(rb.getString(Strings.unknown_error)); // Unknown as this shouldn't happen
				return regReturnData;
			}
			mAddPostAdapter.create(input);
			String okay = mModel.addPost(userEntity, mAddPostAdapter);
			generateAddPostReturnResource(regReturnData, okay);
			return regReturnData;
		} finally {
			mModel.close();
		}
	}

	private void generateAddPostReturnResource(AddPostResourceReturnData regReturnData, String okay) {
		if(okay.equals(PostsModel.ADDED)) {
			regReturnData.setSuccessful(true);
		} else if(okay.equals(PostsModel.BAD_USER_INPUT)) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
		} else if(okay.equals(PostsModel.UNKNOWN_ERROR)){
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		} else {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
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
	
	@GET
	@Path("/{threadId}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists posts within the thread specified", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ListPostsResource listByThreadId(
			@ApiParam(name="threadId") @PathParam("threadId") String threadId,
			@ApiParam(name="start") @PathParam("start") int start, 	
			@ApiParam(name="limit") @PathParam("limit") int limit 	
			) throws IOException {
		List<PostEntity> posts = null;
		try {
			mModel.init();
			posts = mModel.listByThreadId(threadId, start, limit);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} finally {
			mModel.close();
		}
		if(posts==null) {
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			//mModel.close();
			return null;
		} else {
			ListPostsResource adaptedPosts = new ListPostsResourceAdapter(posts);
			//mModel.close();
			return adaptedPosts;
		}
	}
	
	@GET
	@Path("/threads/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists threads, mostly recently created first", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ListPostsResource listThreads(
		@ApiParam(name="start") @PathParam("start") int start, 	
		@ApiParam(name="limit") @PathParam("limit") int limit 	
			) throws IOException {
		List<ThreadEntity> threads = null;
		try {
			mModel.init();
			threads = mModel.listThreads(start, limit);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} finally {
			mModel.close();
		}
		if(threads==null) {
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} else {
			ListPostsResource adaptedPosts = new ListThreadsResourceAdapter(threads);
			return adaptedPosts;
		}
	}	
	
	@GET
	@Path("/threads/{tag}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists threads by tag, mostly recently created first", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ListPostsResource listThreadsByTag(
			@ApiParam(name="tag") @PathParam("tag")  String tag,
		@ApiParam(name="start") @PathParam("start") int start, 	
		@ApiParam(name="limit") @PathParam("limit") int limit 	
			) throws IOException {
		List<ThreadEntity> threads = null;
		try {
			mModel.init();
			threads = mModel.listThreadsByTag(tag, start, limit);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} finally {
			mModel.close();
		}
		if(threads==null) {
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} else {
			ListPostsResource adaptedPosts = new ListThreadsResourceAdapter(threads);
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
	@Path("/edit/{postId}") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Edit a post", notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.posts.resources.EditPostResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})	
	public EditPostResourceReturnData edit(
			@ApiParam(name="postId") @PathParam(value="postId") long postId, 
			@ApiParam(name="editParam") EditPostResource editPostResource) {
		EditPostResourceReturnData ret = new EditPostResourceReturnData();
		try {
			mModel.init();
			ret.setSuccessful(false);
			UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
			if(userEntity==null) {
				ret.setError(rb.getString(Strings.unknown_error)); // Unknown as this shouldn't happen
				return ret;
			}	
			mEditPostAdapter.setPostWithNewData(editPostResource);
			String result = mModel.edit(userEntity, postId, mEditPostAdapter); 
			generateEditReturnResource(ret, result);
			return ret;
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't edit post: " + e.toString());
			ret.setError(rb.getString(Strings.unknown_error));
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

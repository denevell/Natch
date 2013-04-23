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
import org.denevell.natch.serv.posts.PostsModel.AddPostResult;
import org.denevell.natch.serv.posts.PostsModel.DeletePostResult;
import org.denevell.natch.serv.posts.PostsModel.EditPostResult;
import org.denevell.natch.serv.posts.resources.AddPostResourceInput;
import org.denevell.natch.serv.posts.resources.AddPostResourceReturnData;
import org.denevell.natch.serv.posts.resources.DeletePostResourceReturnData;
import org.denevell.natch.serv.posts.resources.EditPostResource;
import org.denevell.natch.serv.posts.resources.EditPostResourceReturnData;
import org.denevell.natch.serv.posts.resources.ListPostsResource;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.Api;
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
	@ApiOperation(value = "Add a post", responseClass="org.denevell.natch.serv.posts.resources.AddPostResourceReturnData")
	public AddPostResourceReturnData add(
			@ApiParam(name="input") AddPostResourceInput input) {
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
		AddPostResult okay = mModel.addPost(userEntity, mAddPostAdapter);
		generateAddPostReturnResource(regReturnData, okay);
		return regReturnData;
	}

	private void generateAddPostReturnResource(AddPostResourceReturnData regReturnData, AddPostResult okay) {
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
			posts = mModel.listByModificationDate(start, limit);
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
	
	@GET
	@Path("/{q}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists posts within the thread specified", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ListPostsResource listByThreadId(
			@ApiParam(name="threadId") @PathParam("q") String threadId,
			@ApiParam(name="start") @PathParam("start") int start, 	
			@ApiParam(name="limit") @PathParam("limit") int limit 	
			) throws IOException {
		List<PostEntity> posts = null;
		try {
			posts = mModel.listByThreadId(threadId, start, limit);
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
			threads = mModel.listThreads(start, limit);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
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
	@Path("/threads/{tag}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists threads by tag, mostly recently created first", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ListPostsResource listThreadsByTag(@ApiParam(name="tag") @PathParam("tag")  String tag) throws IOException {
		List<ThreadEntity> threads = null;
		try {
			threads = mModel.listThreadsByTag(tag);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
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
	@Path("/del/{q}") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete a post", responseClass="org.denevell.natch.serv.posts.resources.DeletePostResourceReturnData")
	public DeletePostResourceReturnData delete(
			@ApiParam(name="postId") @PathParam("q") long number) {
		DeletePostResourceReturnData ret = new DeletePostResourceReturnData();
		ret.setSuccessful(false);
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if(userEntity==null) {
			ret.setError(rb.getString(Strings.unknown_error)); // Unknown as this shouldn't happen
			return ret;
		}	
		try {
			generateDeleteReturnResource(number, ret, userEntity);
			return ret;
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't delete post: " + e.toString());
			ret.setError(rb.getString(Strings.unknown_error));
			return ret;
		} 
	}

	private void generateDeleteReturnResource(long number,
			DeletePostResourceReturnData ret, UserEntity userEntity) {
		DeletePostResult result = mModel.delete(userEntity, number);
		if(result==DeletePostResult.DELETED) {
			ret.setSuccessful(true);
		} else if(result==DeletePostResult.DOESNT_EXIST) {
			ret.setError(rb.getString(Strings.post_doesnt_exist));
		} else if(result==DeletePostResult.NOT_YOURS_TO_DELETE) {
			ret.setError(rb.getString(Strings.post_not_yours));
		} else if(result==DeletePostResult.UNKNOWN_ERROR) {
			ret.setError(rb.getString(Strings.unknown_error));
		}
	}

	@POST
	@Path("/edit/{q}") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Edit a post", responseClass="org.denevell.natch.serv.posts.resources.EditPostResourceReturnData")
	public EditPostResourceReturnData edit(
			@ApiParam(name="postId") @PathParam(value="q") long postId, 
			@ApiParam(name="editParam") EditPostResource editPostResource) {
		EditPostResourceReturnData ret = new EditPostResourceReturnData();
		ret.setSuccessful(false);
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if(userEntity==null) {
			ret.setError(rb.getString(Strings.unknown_error)); // Unknown as this shouldn't happen
			return ret;
		}	
		try {
			mEditPostAdapter.setPostWithNewData(editPostResource);
			EditPostResult result = mModel.edit(userEntity, postId, mEditPostAdapter); 
			generateEditReturnResource(ret, result);
			return ret;
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't edit post: " + e.toString());
			ret.setError(rb.getString(Strings.unknown_error));
			return ret;
		} 		
	}

	private void generateEditReturnResource(EditPostResourceReturnData ret,
			EditPostResult result) {
		if(result==EditPostResult.EDITED) {
			ret.setSuccessful(true);
		} else if(result==EditPostResult.DOESNT_EXIST) {
			ret.setError(rb.getString(Strings.post_doesnt_exist));
		} else if(result==EditPostResult.NOT_YOURS_TO_DELETE) {
			ret.setError(rb.getString(Strings.post_not_yours));
		} else if(result==EditPostResult.UNKNOWN_ERROR) {
			ret.setError(rb.getString(Strings.unknown_error));
		} else if(result==EditPostResult.BAD_USER_INPUT) {
			ret.setError(rb.getString(Strings.post_fields_cannot_be_blank));
		}
	}

}

package org.denevell.natch.serv.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.threads.AddThreadResourceInput;
import org.denevell.natch.io.threads.AddThreadResourceReturnData;
import org.denevell.natch.io.threads.EditThreadResourceInput;
import org.denevell.natch.io.threads.EditThreadResourceReturnData;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.io.threads.ThreadsResource;
import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("threads")
public class ThreadsREST {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private ThreadModel mModel;
	private ResourceBundle rb = Strings.getMainResourceBundle();
	
	public ThreadsREST() {
		mModel = new ThreadModel();
	}
	
	/**
	 * For DI testing.
	 * @param editPostAdapter 
	 */
	public ThreadsREST(ThreadModel postModel, HttpServletRequest request, HttpServletResponse response) {
		mModel = postModel;
		mRequest = request;
		mResponse = response;
	}
	
	@PUT
	@Path("/add") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public AddThreadResourceReturnData add(
			AddThreadResourceInput input) {
		try {
			mModel.init();
			AddThreadResourceReturnData regReturnData = new AddThreadResourceReturnData();
			regReturnData.setSuccessful(false);
			UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
			String okay = mModel.addThread(userEntity, 
					input.getSubject(), input.getContent(), 
					input.getTags());
			generateAddPostReturnResource(regReturnData, okay);
			return regReturnData;
		} finally {
			mModel.close();
		}
	}	
	
	private void generateAddPostReturnResource(AddThreadResourceReturnData regReturnData, String okay) {
		if(okay.equals(PostsModel.BAD_USER_INPUT)) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.post_fields_cannot_be_blank));
		} else if(okay.equals(PostsModel.UNKNOWN_ERROR)){
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		} else { // Should be okay then
			regReturnData.setThreadId(Long.valueOf(okay));
			regReturnData.setSuccessful(true);
		}
	}
	
	@GET
	@Path("/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists threads, mostly recently created first", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ThreadsResource listThreads(
		@ApiParam(name="start") @PathParam("start") int start, 	
		@ApiParam(name="limit") @PathParam("limit") int limit) throws IOException {
		return listThreadWithOrWithoutTag(start, limit, null);
	}

	@GET
	@Path("/{tag}/{start}/{limit}")
	@Produces(MediaType.APPLICATION_JSON)	
	@ApiOperation(value = "Lists threads by tag, mostly recently created first", responseClass="org.denevell.natch.serv.posts.resources.ListPostsResource")
	public ThreadsResource listThreadsByTag(
			@ApiParam(name="tag") @PathParam("tag")  String tag,
			@ApiParam(name="start") @PathParam("start") int start, 	
			@ApiParam(name="limit") @PathParam("limit") int limit 	
			) throws IOException {
		return listThreadWithOrWithoutTag(start, limit, tag);
	}	

	private ThreadsResource listThreadWithOrWithoutTag(int start, int limit, String tag) throws IOException {
		List<ThreadEntity> threads = null;
		long totalPages = -1;
		try {
			mModel.init();
			if(tag==null) {
				threads = mModel.listThreads(start, limit);
			} else {
				threads = mModel.listThreadsByTag(tag, start, limit);
			}
			totalPages = getNumberOfThreadPages(limit, tag);
		} catch(Exception e) {
			Log.info(getClass(), "Couldn't list posts: " + e.toString());
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} finally {
			mModel.close();
		}
		return returnListThreads(start, threads, totalPages, limit);
	}

	public long getNumberOfThreadPages(int limit, String tag) throws Exception {
		long totalThreads = mModel.getTotalNumberOfThreads(tag);
		if(totalThreads==-1) throw new Exception("Couldn't get number of threads");
		double t = (double) totalThreads / (double) limit;
		totalThreads = (long) Math.ceil(t);
		return totalThreads;
	}

	private List<ThreadResource> threadEntitiesToThreadOverview(List<ThreadEntity> threads) {
		List<ThreadResource> ts = new ArrayList<ThreadResource>();
		for (ThreadEntity t: threads) {
			ts.add(new ThreadResource(t.getSubject(), t.getContent(),
					t.getUser().getUsername(), 
					t.getId(),
					t.getCreated(), t.getModified(), 
					null, 
					t.getTags()));
		}
		return ts;
	}	

	public ThreadsResource returnListThreads(
			int start,
			List<ThreadEntity> threads, 
			long totalPages, 
			int limit) throws IOException {
		if(threads==null) {
			mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexcepted error");
			return null;
		} else {
			List<ThreadResource> ts = threadEntitiesToThreadOverview(threads);
			ThreadsResource t = new ThreadsResource(ts, 0, 0);
			long currentPage = getCurrentPage(start, limit);
			t.setPage(currentPage);
			t.setTotalPages(totalPages); 
			return t;
		}
	}

	public long getCurrentPage(int start, int limit) {
		double page = ((double)(start+1) / (double)limit); 
		long currentPage = (long) Math.ceil(page);
		return currentPage;
	}
	
	@POST
	@Path("/edit/{id}") // Explicit for the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Edit a thread", notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.posts.resources.EditPostResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})	
	public EditThreadResourceReturnData edit(
			@ApiParam(name="id") @PathParam(value="id") long postId, 
			@ApiParam(name="editParam") EditThreadResourceInput editPostResource) {
		EditThreadResourceReturnData ret = new EditThreadResourceReturnData();
		try {
			mModel.init();
			ret.setSuccessful(false);
			UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
			String result = mModel.edit(userEntity, postId, null); 
			generateEditReturnResource(ret, result);
			return ret;
		} catch(Exception e) {
			Logger.getLogger(getClass()).error("Couldn't edit thread: ",  e);
			ret.setError(rb.getString(Strings.unknown_error));
			return ret;
		} finally {
			mModel.close();
		} 		
	}

	private void generateEditReturnResource(EditThreadResourceReturnData ret,
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

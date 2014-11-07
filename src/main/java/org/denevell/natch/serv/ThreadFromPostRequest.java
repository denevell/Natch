package org.denevell.natch.serv;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import org.denevell.natch.model.ThreadEntity;
import org.denevell.natch.model.ThreadFromPostModel;
import org.denevell.natch.model.UserGetLoggedInModel;
import org.denevell.natch.model.UserGetLoggedInModel.User;

@Path("thread/frompost")
public class ThreadFromPostRequest {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject ThreadFromPostModel mThreadFromPostModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
	
	// TODO: Return thread id?
	
	public ThreadFromPostRequest() {
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
  public Response addThreadFromPost(@Valid AddThreadFromPostResourceInput input) throws IOException {
    User userEntity = (User) mRequest.getAttribute("user");
    if (!userEntity.admin) {
      return Response.status(403).build();
    }
    ThreadEntity thread = mThreadFromPostModel.makeNewThread(input.postId, input.subject);
    if(thread==null) {
      return Response.serverError().build();
    }
    return Response.ok().build();
  }

  @XmlRootElement
  public static class AddThreadFromPostResourceInput {
    public long postId;
    public String subject;
  }
	
}

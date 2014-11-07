package org.denevell.natch.serv;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.model.PushAddModel;
import org.denevell.natch.model.PushEntity;
import org.denevell.natch.model.PushListModel;

@Path("push")
public class PushIdRequests {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	@Inject PushAddModel mAddModel;
	@Inject PushListModel mListModel;
	
	public PushIdRequests() {
	}
	
	/**
	 * For DI testing.
	 */
	public PushIdRequests(HttpServletRequest request, 
			HttpServletResponse response
			) {
		mRequest = request;
		mResponse = response;
	}
		
	@PUT
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPost(PushInput pushId) {
		PushEntity pushEntity = new PushEntity();
		pushEntity.setClientId(pushId.id);
		mAddModel.add(pushEntity);
		return Response.ok().build(); // Erm
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public PushResource list() {
		List<PushEntity> list = mListModel.list();
		PushResource pr = new PushResource();
		pr.ids = new ArrayList<PushEntity>(list);
		return pr;
	}
	
	public static class PushInput {
	  public String id;
	}

  public static class PushResource {
    public ArrayList<PushEntity> ids = new ArrayList<PushEntity>();
  }
	
}

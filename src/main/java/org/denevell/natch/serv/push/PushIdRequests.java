package org.denevell.natch.serv.push;

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
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.io.base.SuccessOrError;
import org.denevell.natch.io.push.PushInput;
import org.denevell.natch.model.entities.PushEntity;
import org.denevell.natch.model.interfaces.PushAddModel;
import org.denevell.natch.model.interfaces.PushListModel;

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
	public PushIdRequests(CallDbBuilder<PushEntity> postModel, 
			HttpServletRequest request, 
			HttpServletResponse response
			) {
		mRequest = request;
		mResponse = response;
	}
		
	@PUT
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public SuccessOrError addPost(PushInput pushId) {
		PushEntity pushEntity = new PushEntity();
		pushEntity.setClientId(pushId.getId());
		mAddModel.add(pushEntity);
		SuccessOrError successOrError = new SuccessOrError();
		successOrError.setSuccessful(true); // Oh well
		return successOrError;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public PushResource list() {
		List<PushEntity> list = mListModel.list();
		PushResource pr = new PushResource();
		pr.setIds(new ArrayList<PushEntity>(list));
		return pr;
	}
	
}

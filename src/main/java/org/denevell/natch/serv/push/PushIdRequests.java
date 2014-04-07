package org.denevell.natch.serv.push;

import java.util.ArrayList;
import java.util.List;

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
import org.denevell.natch.db.entities.PushEntity;
import org.denevell.natch.io.base.SuccessOrError;

@Path("push")
public class PushIdRequests {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
	private CallDbBuilder<PushEntity> mModel;
	
	public PushIdRequests() {
		mModel = new CallDbBuilder<PushEntity>();
	}
	
	/**
	 * For DI testing.
	 */
	public PushIdRequests(CallDbBuilder<PushEntity> postModel, 
			HttpServletRequest request, 
			HttpServletResponse response
			) {
		mModel = postModel;
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
		mModel
			.startTransaction()
			.queryParam("id", pushId.getId())
			.addIfDoesntExist(PushEntity.NAMED_QUERY_FIND_ID, pushEntity);
		SuccessOrError successOrError = new SuccessOrError();
		successOrError.setSuccessful(true); // Oh well
		return successOrError;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public PushResource list() {
		List<PushEntity> list = mModel
		    .startTransaction()
			.namedQuery(PushEntity.NAMED_QUERY_LIST_IDS)
			.list(PushEntity.class);
		PushResource pr = new PushResource();
		pr.setIds(new ArrayList<PushEntity>(list));
		return pr;
	}
	
}

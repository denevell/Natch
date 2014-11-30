package org.denevell.natch.serv;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
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

import org.denevell.natch.entities.PushEntity;
import org.denevell.natch.entities.PushEntity.AddInput;
import org.denevell.natch.entities.PushEntity.Output;
import org.denevell.natch.model.PushAddModel;
import org.denevell.natch.model.PushListModel;

@Path("push")
public class PushIdRequests {
	
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Inject PushAddModel mAddModel;
	@Inject PushListModel mListModel;
	
	@PUT
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPost(AddInput pushId) {
		PushEntity pushEntity = new PushEntity();
		pushEntity.clientId = pushId.id;
		mAddModel.add(pushEntity);
		return Response.ok().build(); // Erm
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Output list() {
		List<PushEntity> list = mListModel.list();
		Output pr = new Output();
		pr.ids = new ArrayList<PushEntity>(list);
		return pr;
	}
	
}

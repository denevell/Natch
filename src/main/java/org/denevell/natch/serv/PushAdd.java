package org.denevell.natch.serv;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.PushEntity;
import org.denevell.natch.entities.PushEntity.AddInput;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("push_add")
public class PushAdd {
	
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Inject PushAddService mAddModel;
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPost(AddInput pushId) {
		PushEntity pushEntity = new PushEntity();
		pushEntity.clientId = pushId.id;
		return mAddModel.add(pushEntity);
	}

  public static interface PushAddService {
    default Response add(PushEntity pushEntity) {
      return Jrappy2.persist(JPAFactoryContextListener.sFactory, pushEntity);
    }
  }
	
}

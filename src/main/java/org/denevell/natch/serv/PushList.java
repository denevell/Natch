package org.denevell.natch.serv;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.entities.PushEntity;
import org.denevell.natch.entities.PushEntity.Output;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("push_list")
public class PushList {

  @Context HttpServletRequest mRequest;
	@Inject PushListService mListModel;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Output list() {
		List<PushEntity> list = mListModel.list();
		Output pr = new Output();
		pr.ids = new ArrayList<PushEntity>(list);
		return pr;
	}

  public static interface PushListService {
    default List<PushEntity> list() {
      return Jrappy2.list(JPAFactoryContextListener.sFactory, 
          -1, 
          -1, 
          null, 
          PushEntity.class);
    }
  }

}

package org.denevell.natch.gen;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;

@Path("single/{entity}/{id}")
public class ServSingle {

  @Context HttpServletRequest mRequest;

  @GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response single(
	    @PathParam("entity") @NotNull String entity, 	
	    @PathParam("id") String primaryKey,
	    @QueryParam("isLong") @DefaultValue("false") boolean isLong 
	    ) throws Exception {

    Class<?> clazz = Class.forName("org.denevell.natch.entities." + entity);

    // Plus converts to output form if the entity type is enabled for such
    if(isLong) {
      return Jrappy2.find(JPAFactoryContextListener.sFactory, Long.valueOf(primaryKey), false, clazz);
    } else {
      return Jrappy2.find(JPAFactoryContextListener.sFactory, primaryKey, false, clazz);
    }
	}
	
}

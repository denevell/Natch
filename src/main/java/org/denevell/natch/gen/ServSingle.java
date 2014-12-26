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

@Path("single/{entity}")
public class ServSingle {

  @Context HttpServletRequest mRequest;

  @GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response single(
	    @PathParam("entity") @NotNull String entity, 	
	    @QueryParam("idString") String primaryKeyString,
	    @QueryParam("idLong") @DefaultValue("-1") long primaryKeyLong, 
	    @QueryParam("nullField") String paginationField 
	    ) throws Exception {

    Class<?> clazz = Class.forName("org.denevell.natch.entities." + entity);

    Object primaryKey = primaryKeyString;
    if(primaryKeyString==null) {
      primaryKey = primaryKeyLong;
    }
    Response find = Jrappy2.find(
        JPAFactoryContextListener.sFactory, 
        primaryKey,
        paginationField, 
        false, 
        clazz);

    return find;
	}
	
}

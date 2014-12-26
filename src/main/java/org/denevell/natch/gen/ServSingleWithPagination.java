package org.denevell.natch.gen;

import java.lang.reflect.Field;

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

@Path("single_pagination/{entity}")
public class ServSingleWithPagination {

  @Context HttpServletRequest mRequest;

  @GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response single(
	    @PathParam("entity") @NotNull String entity, 	
	    @QueryParam("idString") String primaryKeyString,
	    @QueryParam("idLong") @DefaultValue("-1") long primaryKeyLong,
	    
	    @QueryParam("pageField") String paginationField, 	
	    @QueryParam("pageEntity") String paginationEntity, 	
	    @QueryParam("pageOrderby") String orderby, 	
	    @QueryParam("pageDesc") @DefaultValue("true") boolean desc, 	
	    @QueryParam("pageStart") @DefaultValue("-1") int start, 	
	    @QueryParam("pageLimit") @DefaultValue("-1") int limit,
	    @QueryParam("pageMember") String member,
	    @QueryParam("pageMemberof") String memberOf 
	    ) throws Exception {

    Response single = new ServSingle().single(
        entity, 
        primaryKeyString, 
        primaryKeyLong, 
        paginationField);
    
    if(paginationEntity!=null) {
      Response pagination = new ServList().list(
        paginationEntity, 
        orderby, 
        desc, 
        start, 
        limit, 
        false, 
        null, 
        null);

      Object foundEntity = pagination.getEntity();
      if(foundEntity!=null && paginationField!=null && paginationField.trim().length()>0) {
        Object singleEntity = single.getEntity();
        Class<?> class1 = singleEntity.getClass();
        Field field = class1.getField(paginationField);
        field.set(singleEntity, foundEntity); 
	    }

    }

    return single;
	}
	
}

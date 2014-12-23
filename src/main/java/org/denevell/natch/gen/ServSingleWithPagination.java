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

import jersey.repackaged.com.google.common.collect.Lists;

@Path("single_pagination/{entity}/{id}")
public class ServSingleWithPagination {

  @Context HttpServletRequest mRequest;

  @GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response single(
	    @PathParam("entity") @NotNull String entity, 	
	    @PathParam("id") String primaryKey,
	    @QueryParam("isLong") @DefaultValue("false") boolean isLong,
	    
	    @QueryParam("pageField") String paginationField, 	
	    @QueryParam("pageEntity") String paginationEntity, 	
	    @QueryParam("pageOrderby") String orderby, 	
	    @QueryParam("pageDesc") @DefaultValue("true") boolean desc, 	
	    @QueryParam("pageStart") @DefaultValue("-1") int start, 	
	    @QueryParam("pageLimit") @DefaultValue("-1") int limit,
	    @QueryParam("pageCount") @DefaultValue("false") boolean count,
	    @QueryParam("pageMember") String member,
	    @QueryParam("pageMemberof") String memberOf 
	    ) throws Exception {

    Response single = new ServSingle().single(entity, primaryKey, isLong, paginationField);
    
    if(paginationEntity!=null) {
    Response pagination = new ServList().list(
        paginationEntity, 
        orderby, 
        desc, 
        start, 
        limit, 
        count, 
        member, 
        memberOf);
      
    }

    return single;
	}
	
}

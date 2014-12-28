package org.denevell.natch.gen;

import java.util.ArrayList;
import java.util.List;

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
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.tuple.Pair;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;
import org.denevell.natch.utils.ModelResponse.ModelExternaliser;

@SuppressWarnings({"unchecked", "rawtypes"})
@Path("list/{entity}/")
public class ServList {

  @Context HttpServletRequest mRequest;

  @GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(
	    @PathParam("entity") @NotNull String entity, 	
	    @QueryParam("orderby") String orderby, 	
	    @QueryParam("desc") @DefaultValue("true") boolean desc, 	
	    @QueryParam("start") @DefaultValue("-1") int start, 	
	    @QueryParam("limit") @DefaultValue("-1") int limit,
	    @QueryParam("count") @DefaultValue("false") boolean count,
	    @QueryParam("member") String member,
	    @QueryParam("memberof") String memberOf,
	    @QueryParam("whereField") String whereField,
	    @QueryParam("whereValue") String whereValue 
	    ) throws Exception {

    Class<?> clazz = Class.forName("org.denevell.natch.entities." + entity);

    // Get member of pair for query if specified
    Pair<String, String> memberOfPair = null;
    if(member!=null && member.trim().length()>0 && memberOf !=null && memberOf.trim().length()>0) {
      memberOfPair = Pair.of(member, memberOf);
    }
    Pair<String, String> whereClausePair = null;
    if(whereField!=null && whereField.trim().length()>0 && whereValue !=null && whereValue.trim().length()>0) {
      whereClausePair = Pair.of(whereField, whereValue);
    }

    // Get the list of entities
    List list = Jrappy2.list(
        JPAFactoryContextListener.sFactory, 
        start, 
        limit, 
        orderby, 
        desc, 
        memberOfPair,
        whereClausePair,
        clazz);

    // Convert to output form if the entity type is enabled for such
    if (list != null && list.size() > 0 && list.get(0) instanceof ModelExternaliser) {
      ArrayList<Object> externalisedList = new ArrayList<>();
      for (Object o : list) {
        Object output = ((ModelExternaliser) o).toOutput();
        externalisedList.add(output);
      }
      list = externalisedList;
    } 
    
    // Grab the count of the entire entities if specified
    if(count) {
      long countNum = Jrappy2.count(
          JPAFactoryContextListener.sFactory, 
          whereClausePair, 
          memberOfPair, 
          clazz);
      // Get the latest entry via a list call
      return Response.ok(new OutputWithCount(list, countNum)).build();
    } else {
      return Response.ok(list).build();
    }
    
	}

  @XmlRootElement
	public static class OutputWithCount<T> {
	  public List<T> results;
	  public long count = - 1;
	  public OutputWithCount() {}
	  public OutputWithCount(List<T> results, long count) {
	    this.results = results;
	    this.count = count;
	  }
	}
	
}

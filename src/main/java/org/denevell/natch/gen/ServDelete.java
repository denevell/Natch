package org.denevell.natch.gen;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;
import org.denevell.natch.utils.ModelResponse;
import org.denevell.natch.utils.UserGetLoggedInService.Admin;
import org.denevell.natch.utils.UserGetLoggedInService.Username;

@Path("delete/{entity}")
public class ServDelete {

  @Context public HttpServletRequest mRequest;

  @DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(
	    @PathParam("entity") @NotNull String entity, 	
	    @QueryParam("idString") String primaryKeyString,
	    @QueryParam("idLong") long primaryKeyLong,
	    @QueryParam("authObject") String authField
	    ) throws Exception {
    
    // Get username, and give a 401 if null, which it shouldn't be
    Username user = (Username) mRequest.getAttribute("user");
    Admin admin = (Admin) user;
    if(admin == null || user == null) {
      return new ModelResponse<Void>(401, null).httpReturn();
    }

    // Now remove it, check if the authObject, or main root object's username 
    // is our username or we're admin
    Class<?> clazz = Class.forName("org.denevell.natch.entities." + entity);
    Object primaryKey = (primaryKeyString!=null) ? primaryKeyString : primaryKeyLong;
    return Jrappy2.remove(
        JPAFactoryContextListener.sFactory, 
        primaryKey, 
        (thread) -> {
          try {
            return admin.getAdmin() || getUsernameField(thread, authField).equals(user.getUsername());
          } catch (Exception e) {
            Logger.getLogger(ServDelete.class).info("Couldn't find the delete entity's username field", e);
            return false;
          }
        }, 
        clazz);
  }

  private Object getUsernameField(Object rootObject, String authField) throws Exception {
    if(authField!=null && authField.trim().length()>0) {
      Field field = rootObject.getClass().getField(authField);
      Object ob = field.get(rootObject);
      return ((Username)ob).getUsername();
    } else {
      return ((Username)rootObject).getUsername();
    }
  }
	
}

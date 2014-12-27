package org.denevell.natch.gen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.denevell.natch.utils.Adapter.AdapterEdit;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;
import org.denevell.natch.utils.ModelResponse;
import org.denevell.natch.utils.UserGetLoggedInService.SystemUser;
import org.denevell.natch.utils.UserGetLoggedInService.Username;
import org.glassfish.jersey.server.validation.ValidationError;

@Path("update/{entity}/{entityInput}")
@SuppressWarnings("unchecked")
public class ServUpdate {

  @Context HttpServletRequest mRequest;

  @POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(
	    @PathParam("entity") @NotNull String entity, 	
	    @PathParam("entityInput") @NotNull String entityInput, 	
	    @QueryParam("idLong") long primaryKeyLong,
	    @QueryParam("idString") String primaryKeyString,
	    @QueryParam("authObject") String authField,
	    String editInput
	    ) throws Exception {
    
    // Get username, and give a 401 if null, which it shouldn't be
    SystemUser user = (SystemUser) mRequest.getAttribute("user");
    if(user == null) {
      return new ModelResponse<Void>(401, null).httpReturn();
    }

    // Get the input class and read its values from the json
	  Class<?> inputClass = Class.forName("org.denevell.natch.entities."+entityInput);
	  ObjectMapper objectMapper = new ObjectMapper();
    Object inputObject = objectMapper.readValue(editInput, inputClass);
    AdapterEdit adapterEdit = (AdapterEdit) inputObject;
    // TODO: Convert to adatper to returns something for input
    
    // Add bean validation to the input object
    Set<ConstraintViolation<Object>> validations = Validation.buildDefaultValidatorFactory().getValidator().validate(inputObject);
    List<ValidationError> validationErrors = new ArrayList<>();
    for (ConstraintViolation<Object> constraintViolation : validations) {
      ValidationError ve = new ValidationError();
      ve.setMessage(constraintViolation.getMessage());
      ve.setPath("hmm");
      Object invalidValue = constraintViolation.getInvalidValue();
      if(invalidValue!=null) {
        ve.setInvalidValue(invalidValue.toString());
      }
      validationErrors.add(ve);
    }
    if(validations!=null && validations.size()>0) {
      return new ModelResponse<List<ValidationError>>(400, validationErrors).httpReturn();
    }

    // Now update it, check if the authObject, or main root object's username 
    // is our username or we're admin
    Class<?> clazz = Class.forName("org.denevell.natch.entities." + entity);
    Object primaryKey = (primaryKeyLong>0) ? primaryKeyLong : primaryKeyString;
    Response update = Jrappy2.update(
        JPAFactoryContextListener.sFactory, 
        clazz,
        primaryKey, 
        (item) -> { // Do we have access rights?
          try { 
            return user.getAdmin() || getUsernameField(item, authField).equals(user.getUsername());
          } catch (Exception e) {
            Logger.getLogger(ServUpdate.class).info("Couldn't find the delete entity's username field", e);
            return false;
          }
        }, 
        (item) -> {
          Object updateEntity = adapterEdit.updateEntity(item, user);
          return updateEntity;
        });
    return update;
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

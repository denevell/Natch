package org.denevell.natch.gen;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.denevell.natch.utils.Adapter;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Jrappy2;
import org.denevell.natch.utils.ModelResponse.PushResourceExternaliser;
import org.denevell.natch.utils.PushSendService;

@Path("add/{inputClass}")
@SuppressWarnings({"rawtypes", "unchecked"})
public class ServAdd{
	
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	
  @PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPost(
	    @PathParam("inputClass") String inputClassName,
	    @QueryParam("push") String pushSend,
	    String input
	    ) throws Exception {
	 
    // Get the input class and read its values from the json
	  Class<?> inputClass = Class.forName("org.denevell.natch.entities."+inputClassName);
	  ObjectMapper objectMapper = new ObjectMapper();
    Object inputObject = objectMapper.readValue(input, inputClass);
    
    // Add bean validation to the input object
    Set<ConstraintViolation<Object>> validations = Validation.buildDefaultValidatorFactory().getValidator().validate(inputObject);
    for (ConstraintViolation<Object> constraintViolation : validations) {
      int i = 0;
    }
	  
	  // Persist it
    Response persist = Jrappy2.persist(JPAFactoryContextListener.sFactory, ((Adapter)inputObject).adapt());

    // Send the push if needed
    if (pushSend!=null && pushSend.length()>0 && persist.getStatus() == 200) {
      Object object = persist.getEntity();
      if(object instanceof PushResourceExternaliser) {
        object = ((PushResourceExternaliser)object).toPushResource(object);
        PushSendService.sendPushNotifications(pushSend, object);
      } else {
        Logger.getLogger(getClass()).info("Request a push send but entity doesn't implement PushResourceExternaliser");
      }
    }

    return persist;
	}

}

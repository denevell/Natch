package org.denevell.natch.serv.users;

import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserAddModel;
import org.denevell.natch.utils.Strings;


@Path("user")
public class RegisterRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
    ResourceBundle rb = Strings.getMainResourceBundle();
    @Inject UserAddModel mUserAddModel;
	
	public RegisterRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public RegisterRequest(HttpServletRequest request) {
		mRequest = request;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RegisterResourceReturnData register(@Valid RegisterResourceInput registerInput) {
		RegisterResourceReturnData regReturnData = new RegisterResourceReturnData();
		UserEntity u = new UserEntity(registerInput);
		int added = mUserAddModel.add(u);
		if(added==UserAddModel.EMAIL_ALREADY_EXISTS) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.email_already_exists));
			return regReturnData;
		} else if (added==UserAddModel.USER_ALREADY_EXISTS) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.username_already_exists));
		} else {
			regReturnData.setSuccessful(true);
		}
		return regReturnData;
	}	
	
}

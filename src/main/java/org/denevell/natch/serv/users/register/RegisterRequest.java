package org.denevell.natch.serv.users.register;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;


@Path("user")
public class RegisterRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	private RegisterModel mModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public RegisterRequest() {
		mModel = new RegisterModel();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public RegisterRequest(RegisterModel userModel, HttpServletRequest request) {
		mModel = userModel;
		mRequest = request;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Registers a user", responseClass="org.denevell.natch.serv.users.resources.RegisterResourceReturnData")
	public RegisterResourceReturnData register(
			@ApiParam(name="registerInput") RegisterResourceInput registerInput) {
		RegisterResourceReturnData regReturnData = new RegisterResourceReturnData();
		if (registerInput == null) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.user_pass_cannot_be_blank));
			return regReturnData;
		}
		String okay = mModel.addUserToSystem(registerInput.getUsername(),
				registerInput.getPassword());
		if (okay.equals(RegisterModel.REGISTERED)) {
			regReturnData.setSuccessful(true);
		} else if (okay.equals(RegisterModel.USER_INPUT_ERROR)) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.user_pass_cannot_be_blank));
		} else if (okay.equals(RegisterModel.DUPLICATE_USERNAME)) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.username_already_exists));
		} else {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.unknown_error));
		}
		return regReturnData;
	}	
	
}

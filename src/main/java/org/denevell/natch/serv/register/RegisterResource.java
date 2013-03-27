package org.denevell.natch.serv.register;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.serv.register.RegisterModel.RegisterResult;
import org.denevell.natch.utils.Strings;


@Path("register")
public class RegisterResource {
	
	@Context UriInfo info;
	@Context HttpServletRequest request;
	@Context ServletContext context;
	private RegisterModel mUserModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public RegisterResource() {
		mUserModel = new RegisterModel();
	}
	
	/**
	 * For DI testing.
	 */
	public RegisterResource(RegisterModel userModel) {
		mUserModel = userModel;
	}
	
	@DELETE
	public void clearTestDb() {
		mUserModel.clearTestDb();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RegisterResourceReturnData register(RegisterResourceInput registerInput) {
		RegisterResourceReturnData regReturnData = new RegisterResourceReturnData();
		if(registerInput==null) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString("user_pass_cant_be_blank"));
			return regReturnData;
		}
		RegisterResult okay = mUserModel.addUserToSystem(registerInput.getUsername(), registerInput.getPassword());
		if(okay==RegisterResult.REGISTERED) {
			regReturnData.setSuccessful(true);
		} else if(okay==RegisterResult.USER_INPUT_ERROR) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString("user_pass_cant_be_blank"));
		} else if(okay==RegisterResult.DUPLICATE_USERNAME){
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString("username_exists"));
		} else {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString("unknown_error"));
		}
		return regReturnData;
	}

}

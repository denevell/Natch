package org.denevell.natch.register;

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

import org.denevell.natch.register.RegisterModel.RegisterResult;


@Path("user")
public class RegisterResource {
	
	@Context UriInfo info;
	@Context HttpServletRequest request;
	@Context ServletContext context;
	private RegisterModel mUserModel;
	
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
		mUserModel.clearTestDd();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RegisterResourceReturnData register(RegisterResourceInput registerInput) {
		RegisterResourceReturnData regReturnData = new RegisterResourceReturnData();
		if(!isValidRegisterInput(registerInput)) {
			regReturnData.setSuccessful(false);
			regReturnData.setError("Username and password cannot be blank.");
			return regReturnData;
		} else {
			RegisterResult okay = RegisterResult.UNKNOWN_ERROR;
			okay = mUserModel.addUserToSystem(registerInput.getUsername(), registerInput.getPassword());
			if(okay==RegisterResult.REGISTERED) {
				regReturnData.setSuccessful(true);
			} else {
				regReturnData.setSuccessful(false);
				regReturnData.setError("Username already exists.");
			}
			return regReturnData;
		} 
	}

	private boolean isValidRegisterInput(RegisterResourceInput registerInput) {
		return registerInput!=null 
				&& registerInput.getPassword()!=null 
				&& registerInput.getPassword().trim().length()!=0 
				&& registerInput.getUsername()!=null
				&& registerInput.getUsername().trim().length()!=0;
	}
}

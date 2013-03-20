package org.denevell.natch.register;

import java.net.HttpURLConnection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
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
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RegisterResourceReturnData register(RegisterResourceInput registerInput) {
		RegisterResourceReturnData regReturnData = new RegisterResourceReturnData();
		RegisterResult okay = RegisterResult.UNKNOWN_ERROR;
		if(registerInput!=null) {
			okay = mUserModel.addUserToSystem(registerInput.getUsername(), registerInput.getPassword());
			if(okay==RegisterResult.REGISTERED) {
				regReturnData.setSuccessful(true);
			} else {
				regReturnData.setSuccessful(false);
			}
		} 
		if(okay==RegisterResult.UNKNOWN_ERROR) {
			throw new WebApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
		}
		return regReturnData;
	}
}

package org.denevell.natch.serv.users.status;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.io.users.LoginResourceLoggedInReturnData;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("user/is")
@Api(value="/user", description="Register, login, logout and see if a user is logged in.")
public class StatusRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	private StatusModel mLoginModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public StatusRequest() {
		mLoginModel = new StatusModel();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public StatusRequest(StatusModel userModel, HttpServletRequest request) {
		mLoginModel = userModel;
		mRequest = request;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Check if logged in", 
		notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.users.resources.LoginResourceLoggedInReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})
	public LoginResourceLoggedInReturnData isLoggedIn() {
		try {
			mLoginModel.init();
			LoginResourceLoggedInReturnData ret = new LoginResourceLoggedInReturnData();
			ret.setSuccessful(true);
			return ret;
		} finally {
			mLoginModel.close();
		}
	}

}

package org.denevell.natch.serv.users.logout;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.io.users.LogoutResourceReturnData;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("user/logout")
@Api(value="/user", description="Register, login, logout and see if a user is logged in.")
public class LogoutRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	private LogoutModel mLoginModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public LogoutRequest() {
		mLoginModel = new LogoutModel();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public LogoutRequest(LogoutModel userModel, HttpServletRequest request) {
		mLoginModel = userModel;
		mRequest = request;
	}
		
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Logout", 
		notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.users.resources.LogoutResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})
	public LogoutResourceReturnData logout() {
		try {
			mLoginModel.init();
			LogoutResourceReturnData returnResult = new LogoutResourceReturnData();
			String authKey = mRequest.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY).toString();
			if(mLoginModel.logout(authKey)) {
				returnResult.setSuccessful(true);
			} else {
				returnResult.setSuccessful(false);
				returnResult.setError(rb.getString(Strings.unknown_error));
			}
			return returnResult;
		} finally {
			mLoginModel.close();
		}
	}	

}

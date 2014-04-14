package org.denevell.natch.serv.users;

import java.util.ResourceBundle;

import javax.inject.Inject;
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
import org.denevell.natch.model.interfaces.UserLogoutModel;
import org.denevell.natch.utils.Strings;

@Path("user/logout")
public class LogoutRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Inject UserLogoutModel mUserLogoutModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public LogoutRequest() {
	}
	
	/**
	 * For DI testing.
	 */
	public LogoutRequest(UserLogoutModel model, HttpServletRequest request) {
		mUserLogoutModel = model;
		mRequest = request;
	}
		
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public LogoutResourceReturnData logout() {
		LogoutResourceReturnData returnResult = new LogoutResourceReturnData();
		String authKey = mRequest.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY).toString();
		int logout = mUserLogoutModel.logout(authKey);
		if(logout==UserLogoutModel.SUCCESS) {
			returnResult.setSuccessful(true);
		}
		return returnResult;
	}	

}

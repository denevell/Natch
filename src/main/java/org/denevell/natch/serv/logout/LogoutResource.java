package org.denevell.natch.serv.logout;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;


@Path("logout")
public class LogoutResource {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext mContext;
	private LogoutModel mLogoutModel;
	
	public LogoutResource() {
		mLogoutModel = new LogoutModel();
	}
	
	/**
	 * For DI testing.
	 */
	public LogoutResource(LogoutModel userModel, HttpServletRequest request) {
		mLogoutModel = userModel;
		mRequest = request;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public LogoutResourceReturnData logout() {
		LogoutResourceReturnData returnResult = new LogoutResourceReturnData();
		String authKey = mRequest.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY).toString();
		if(mLogoutModel.logout(authKey)) {
			returnResult.setSuccessful(true);
		} else {
			returnResult.setSuccessful(false);
			returnResult.setError("Unknown error.");
		}
		return returnResult;
	}
}

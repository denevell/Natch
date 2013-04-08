package org.denevell.natch.serv.logout;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.serv.logout.resources.LogoutResourceReturnData;
import org.denevell.natch.utils.Strings;


@Path("logout")
public class LogoutREST {
	
	@Context UriInfo mInfo;
	@Context HttpServletRequest mRequest;
	@Context ServletContext mContext;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LogoutModel mLogoutModel;
	
	public LogoutREST() {
		mLogoutModel = new LogoutModel();
	}
	
	/**
	 * For DI testing.
	 */
	public LogoutREST(LogoutModel userModel, HttpServletRequest request) {
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
			returnResult.setError(rb.getString(Strings.unknown_error));
		}
		return returnResult;
	}
}

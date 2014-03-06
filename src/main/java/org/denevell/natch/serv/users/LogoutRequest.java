package org.denevell.natch.serv.users;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.users.LogoutResourceReturnData;
import org.denevell.natch.utils.Strings;

@Path("user/logout")
public class LogoutRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginAuthKeysSingleton mAuthDataGenerator;
	
	public LogoutRequest() {
		mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public LogoutRequest(LoginAuthKeysSingleton loginAuth, HttpServletRequest request) {
		mAuthDataGenerator = loginAuth;
		mRequest = request;
	}
		
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public LogoutResourceReturnData logout() {
		LogoutResourceReturnData returnResult = new LogoutResourceReturnData();
		String authKey = mRequest.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY).toString();
		if(authKey!=null && authKey.trim().length()!=0) {
			mAuthDataGenerator.remove(authKey);
			UserEntity username = mAuthDataGenerator.retrieveUserEntity(authKey);
			if(username == null) {
				returnResult.setSuccessful(true);
			} 
		}
		return returnResult;
	}	

}

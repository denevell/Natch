package org.denevell.natch.login;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.login.LoginModel.LoginEnumResult;
import org.denevell.natch.login.LoginModel.LoginResult;


@Path("login")
public class LoginResource {
	
	@Context UriInfo info;
	@Context HttpServletRequest request;
	@Context ServletContext context;
	private LoginModel mLoginModel;
	
	public LoginResource() {
		mLoginModel = new LoginModel();
	}
	
	/**
	 * For DI testing.
	 */
	public LoginResource(LoginModel userModel) {
		mLoginModel = userModel;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public LoginResourceReturnData login(LoginResourceInput loginInput) {
		LoginResourceReturnData returnResult = new LoginResourceReturnData();
		if(loginInput==null) {
			returnResult.setSuccessful(false);
			returnResult.setError("Incorrect username or password.");
			return returnResult;
		}
		String username = loginInput.getUsername();
		String password = loginInput.getPassword();
		LoginResult loginResult = mLoginModel.login(username, password);
		if(loginResult.getResult()==LoginEnumResult.LOGGED_IN) {
			returnResult.setSuccessful(true);
			returnResult.setAuthKey(loginResult.getAuthKey());
		} else {
			returnResult.setSuccessful(false);
			returnResult.setError("Incorrect username or password.");
			
		}
		return returnResult;
	}

}

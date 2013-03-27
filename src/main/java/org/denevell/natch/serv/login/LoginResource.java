package org.denevell.natch.serv.login;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.serv.login.LoginModel.LoginEnumResult;
import org.denevell.natch.serv.login.LoginModel.LoginResult;
import org.denevell.natch.utils.Strings;


@Path("login")
public class LoginResource {
	
	@Context UriInfo info;
	@Context HttpServletRequest request;
	@Context ServletContext context;
	private LoginModel mLoginModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
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
			returnResult.setError(rb.getString("incorrect_username"));
			return returnResult;
		}
		String username = loginInput.getUsername();
		String password = loginInput.getPassword();
		LoginResult loginResult = mLoginModel.login(username, password);
		if(loginResult.getResult()==LoginEnumResult.LOGGED_IN) {
			returnResult.setSuccessful(true);
			returnResult.setAuthKey(loginResult.getAuthKey());
		} else if(loginResult.getResult()==LoginEnumResult.CREDENTIALS_INCORRECT
				|| loginResult.getResult()==LoginEnumResult.USER_INPUT_ERROR){
			returnResult.setSuccessful(false);
			returnResult.setError(rb.getString("incorrect_username"));
		} else {
			returnResult.setSuccessful(false);
			returnResult.setError(rb.getString("unknown_error"));
		}
		return returnResult;
	}

	@GET
	@Path("is/{k}")
	@Produces(MediaType.APPLICATION_JSON)
	public LoginResourceLoggedInReturnData isLoggedIn(@PathParam("k") String authKey) {
		LoginResourceLoggedInReturnData ret = new LoginResourceLoggedInReturnData();
		String username = mLoginModel.loggedInAs(authKey);
		if(username==null) {
			ret.setSuccessful(false);
		} else {
			ret.setSuccessful(true);
		}
		return ret;
	}

}

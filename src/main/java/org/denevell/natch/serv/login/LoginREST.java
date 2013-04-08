package org.denevell.natch.serv.login;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.serv.login.LoginModel.LoginEnumResult;
import org.denevell.natch.serv.login.LoginModel.LoginResult;
import org.denevell.natch.serv.login.resources.LoginResourceInput;
import org.denevell.natch.serv.login.resources.LoginResourceLoggedInReturnData;
import org.denevell.natch.serv.login.resources.LoginResourceReturnData;
import org.denevell.natch.utils.Strings;


@Path("login")
public class LoginREST {
	
	@Context UriInfo info;
	@Context HttpServletRequest request;
	@Context ServletContext context;
	private LoginModel mLoginModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public LoginREST() {
		mLoginModel = new LoginModel();
	}
	
	/**
	 * For DI testing.
	 */
	public LoginREST(LoginModel userModel) {
		mLoginModel = userModel;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public LoginResourceReturnData login(LoginResourceInput loginInput) {
		LoginResourceReturnData returnResult = new LoginResourceReturnData();
		if(loginInput==null) {
			returnResult.setSuccessful(false);
			returnResult.setError(rb.getString(Strings.incorrect_username_or_password));
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
			returnResult.setError(rb.getString(Strings.incorrect_username_or_password));
		} else {
			returnResult.setSuccessful(false);
			returnResult.setError(rb.getString(Strings.unknown_error));
		}
		return returnResult;
	}

	@GET
	@Path("is")
	@Produces(MediaType.APPLICATION_JSON)
	public LoginResourceLoggedInReturnData isLoggedIn() {
		// If we get here, the login filter failed.
		LoginResourceLoggedInReturnData ret = new LoginResourceLoggedInReturnData();
		ret.setSuccessful(true);
		return ret;
	}

}

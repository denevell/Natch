package org.denevell.natch.serv.users.login;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.serv.users.login.LoginModel.LoginResult;
import org.denevell.natch.utils.Strings;


@Path("user/login")
public class LoginRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	private LoginModel mLoginModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public LoginRequest() {
		mLoginModel = new LoginModel();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public LoginRequest(LoginModel userModel, HttpServletRequest request) {
		mLoginModel = userModel;
		mRequest = request;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public LoginResourceReturnData login(@Valid LoginResourceInput loginInput) {
		try {
			mLoginModel.init();
			LoginResourceReturnData returnResult = new LoginResourceReturnData();
			String username = loginInput.getUsername();
			String password = loginInput.getPassword();
			LoginResult loginResult = mLoginModel.login(username, password);
			if(loginResult.getResult().equals(LoginModel.LOGGED_IN)) {
				returnResult.setSuccessful(true);
				returnResult.setAdmin(loginResult.isAdmin());
				returnResult.setAuthKey(loginResult.getAuthKey());
			} else if(loginResult.getResult().equals(LoginModel.CREDENTIALS_INCORRECT)){
				returnResult.setSuccessful(false);
				returnResult.setError(rb.getString(Strings.incorrect_username_or_password));
			} 
			return returnResult;
		} finally {
			mLoginModel.close();
		}
	}


}

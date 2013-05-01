package org.denevell.natch.serv.users;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceLoggedInReturnData;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.LogoutResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.serv.users.UsersModel.LoginResult;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;


@Path("user")
@Api(value="/user", description="Register, login, logout and see if a user is logged in.")
public class UsersREST {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	private UsersModel mLoginModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public UsersREST() {
		mLoginModel = new UsersModel();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public UsersREST(UsersModel userModel, HttpServletRequest request) {
		mLoginModel = userModel;
		mRequest = request;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Registers a user", responseClass="org.denevell.natch.serv.users.resources.RegisterResourceReturnData")
	public RegisterResourceReturnData register(
			@ApiParam(name="registerInput") RegisterResourceInput registerInput) {
		RegisterResourceReturnData regReturnData = new RegisterResourceReturnData();
		try {
			if(registerInput==null) {
				regReturnData.setSuccessful(false);
				regReturnData.setError(rb.getString(Strings.user_pass_cannot_be_blank));
				return regReturnData;
			}
			String okay = mLoginModel.addUserToSystem(registerInput.getUsername(), registerInput.getPassword());
			if(okay.equals(UsersModel.REGISTERED)) {
				regReturnData.setSuccessful(true);
			} else if(okay.equals(UsersModel.USER_INPUT_ERROR)) {
				regReturnData.setSuccessful(false);
				regReturnData.setError(rb.getString(Strings.user_pass_cannot_be_blank));
			} else if(okay.equals(UsersModel.DUPLICATE_USERNAME)){
				regReturnData.setSuccessful(false);
				regReturnData.setError(rb.getString(Strings.username_already_exists));
			} else {
				regReturnData.setSuccessful(false);
				regReturnData.setError(rb.getString(Strings.unknown_error));
			}
			return regReturnData;
		} finally {
			mLoginModel.close();
		}
	}	
	
	@POST
	@Path("/login") // So we can url match for servlet filters
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Login",  notes="You use the return AuthKey for the future requests which require a AuthKey header",
		responseClass="org.denevell.natch.serv.users.resources.LoginResourceReturnData")
	public LoginResourceReturnData login(@ApiParam(name="loginInput") LoginResourceInput loginInput) {
		try {
			LoginResourceReturnData returnResult = new LoginResourceReturnData();
			if(loginInput==null) {
				returnResult.setSuccessful(false);
				returnResult.setError(rb.getString(Strings.incorrect_username_or_password));
				return returnResult;
			}
			String username = loginInput.getUsername();
			String password = loginInput.getPassword();
			LoginResult loginResult = mLoginModel.login(username, password);
			if(loginResult.getResult().equals(UsersModel.LOGGED_IN)) {
				returnResult.setSuccessful(true);
				returnResult.setAuthKey(loginResult.getAuthKey());
			} else if(loginResult.getResult().equals(UsersModel.CREDENTIALS_INCORRECT)
					|| loginResult.getResult().equals(UsersModel.USER_INPUT_ERROR)){
				returnResult.setSuccessful(false);
				returnResult.setError(rb.getString(Strings.incorrect_username_or_password));
			} else {
				returnResult.setSuccessful(false);
				returnResult.setError(rb.getString(Strings.unknown_error));
			}
			return returnResult;
		} finally {
			mLoginModel.close();
		}
	}

	@GET
	@Path("/is")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Check if logged in", 
		notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.users.resources.LoginResourceLoggedInReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})
	public LoginResourceLoggedInReturnData isLoggedIn() {
		// If we get here, the login filter failed.
		LoginResourceLoggedInReturnData ret = new LoginResourceLoggedInReturnData();
		ret.setSuccessful(true);
		mLoginModel.close();
		return ret;
	}
	
	@DELETE
	@Path("/logout") // So we can url match a 'logout' in the servlet filter
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Logout", 
		notes="Must contain the AuthKey header.",
		responseClass="org.denevell.natch.serv.users.resources.LogoutResourceReturnData")
	@ApiErrors({
		@ApiError(code=401, reason="Incorrect AuthKey header.")
	})
	public LogoutResourceReturnData logout() {
		LogoutResourceReturnData returnResult = new LogoutResourceReturnData();
		String authKey = mRequest.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY).toString();
		if(mLoginModel.logout(authKey)) {
			returnResult.setSuccessful(true);
		} else {
			returnResult.setSuccessful(false);
			returnResult.setError(rb.getString(Strings.unknown_error));
		}
		mLoginModel.close();
		return returnResult;
	}	

}

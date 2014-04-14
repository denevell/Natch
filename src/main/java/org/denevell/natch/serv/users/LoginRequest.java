package org.denevell.natch.serv.users;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserLoginModel;
import org.denevell.natch.model.interfaces.UserLoginModel.UserEntityAndAuthKey;
import org.denevell.natch.utils.Strings;

@Path("user/login")
public class LoginRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Context ServletContext context;
    ResourceBundle rb = Strings.getMainResourceBundle();
	@Inject UserLoginModel mModel;
	
	public LoginRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public LoginRequest(CallDbBuilder<UserEntity> userModel, HttpServletRequest request) {
		mRequest = request;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public LoginResourceReturnData login(@Valid LoginResourceInput loginInput) throws IOException {
		LoginResourceReturnData returnResult = new LoginResourceReturnData();
		UserEntityAndAuthKey res = mModel.login(loginInput.getUsername(), loginInput.getPassword());
		if(res==null) {
            mResponse.sendError(HttpServletResponse.SC_FORBIDDEN); 
            return null;
		} else {
			returnResult.setSuccessful(true);
			returnResult.setAdmin(res.userEntity.isAdmin());
			returnResult.setAuthKey(res.authKey);
			return returnResult;
		} 
	}

}

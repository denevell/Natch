package org.denevell.natch.serv.users;

import java.io.IOException;
import java.util.ResourceBundle;

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

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.utils.PasswordSaltUtils;
import org.denevell.natch.utils.Strings;

@Path("user/login")
public class LoginRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Context ServletContext context;
	private CallDbBuilder<UserEntity> mLoginModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginAuthKeysSingleton mAuthDataGenerator;
	private PasswordSaltUtils mSaltedPasswordUtils;
	
	public LoginRequest() {
		mLoginModel = new CallDbBuilder<UserEntity>();
		mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
		mSaltedPasswordUtils = new PasswordSaltUtils();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public LoginRequest(CallDbBuilder<UserEntity> userModel, HttpServletRequest request) {
		mLoginModel = userModel;
		mRequest = request;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public LoginResourceReturnData login(@Valid LoginResourceInput loginInput) throws IOException {
		LoginResourceReturnData returnResult = new LoginResourceReturnData();
		UserEntity res = mLoginModel
				.startTransaction()
				.namedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME)
				.queryParam("username", loginInput.getUsername())
				.single(UserEntity.class);
		if(res==null) {
            mResponse.sendError(HttpServletResponse.SC_FORBIDDEN); 
            return null;
		}
		if (mSaltedPasswordUtils.checkSaltedPassword(loginInput.getPassword(), res.getPassword())) {
			String authKey = mAuthDataGenerator.generate(res);
			returnResult.setSuccessful(true);
			returnResult.setAdmin(res.isAdmin());
			returnResult.setAuthKey(authKey);
			return returnResult;
		} else {
            mResponse.sendError(HttpServletResponse.SC_FORBIDDEN); 
            return null;
		}
	}

}

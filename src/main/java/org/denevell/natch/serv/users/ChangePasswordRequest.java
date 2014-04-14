package org.denevell.natch.serv.users;

import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.io.users.ChangePasswordInput;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserChangePasswordModel;
import org.denevell.natch.utils.Strings;

@Path("user/password")
public class ChangePasswordRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Context ServletContext context;
    ResourceBundle rb = Strings.getMainResourceBundle();
    @Inject UserChangePasswordModel mUserChangePassword;
	
	public ChangePasswordRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public ChangePasswordRequest(CallDbBuilder<UserEntity> userModel, HttpServletRequest request) {
		mRequest = request;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void changePassword(@Valid final ChangePasswordInput changePass) throws Exception {
		final UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		int res = mUserChangePassword.changePassword(userEntity.getUsername(), changePass.getPassword());
		if(res==UserChangePasswordModel.NOT_FOUND) mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
	}

	@POST
	@Path("/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void changePasswordAsAdmin(
			@PathParam("username") String username,
			@Valid final ChangePasswordInput changePass) throws Exception {
		final UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if(!userEntity.isAdmin()) {
			mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED); 
			return;
		}
		int res = mUserChangePassword.changePassword(username, changePass.getPassword());
		if(res==UserChangePasswordModel.NOT_FOUND) mResponse.sendError(HttpServletResponse.SC_NOT_FOUND); 
	}
}

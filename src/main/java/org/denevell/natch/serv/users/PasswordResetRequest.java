package org.denevell.natch.serv.users;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserPasswordResetDeleteModel;
import org.denevell.natch.model.interfaces.UserPasswordResetRequestModel;
import org.denevell.natch.utils.Strings;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;


@Path("user/password_reset")
public class PasswordResetRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
	@Context HttpServletResponse mResponse;
    ResourceBundle rb = Strings.getMainResourceBundle();
    @Inject UserPasswordResetRequestModel mUserModelRequest;
    @Inject UserPasswordResetDeleteModel mUserModelDelete;
	
	public PasswordResetRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public PasswordResetRequest(CallDbBuilder<UserEntity> userModel, HttpServletRequest request) {
		mRequest = request;
	}
	
	@POST
	@Path("/{recoveryEmail}")
	public void requestReset(@PathParam("recoveryEmail") @NotEmpty @NotBlank String recoveryEmail) throws IOException {
		int result = mUserModelRequest.requestReset(recoveryEmail);
	    if(result==UserPasswordResetRequestModel.EMAIL_NOT_FOUND) {
	    	mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
	    	return;
	    } 
	}	

	@DELETE
	@Path("remove/{username}")
	public void requestNoReset(@PathParam("username") @NotEmpty @NotBlank String username) throws IOException {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if(!userEntity.isAdmin()) {
			mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		int result = mUserModelDelete.deleteRequest(username);
		if(result==UserPasswordResetDeleteModel.CANT_FIND) {
			mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}	
	
}

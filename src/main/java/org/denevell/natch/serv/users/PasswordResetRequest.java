package org.denevell.natch.serv.users;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.Strings;


@Path("user/password/reset")
public class PasswordResetRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private CallDbBuilder<UserEntity> mModel;
	
	public PasswordResetRequest() {
		mModel = new CallDbBuilder<UserEntity>();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public PasswordResetRequest(CallDbBuilder<UserEntity> userModel, HttpServletRequest request) {
		mModel = userModel;
		mRequest = request;
	}
	
	@POST
	public void requestReset() {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		userEntity.setPasswordResetRequest(true);
		mModel.update(userEntity);
	}	

	@DELETE
	public void requestNoReset() {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		userEntity.setPasswordResetRequest(false);
		mModel.update(userEntity);
	}	
	
}

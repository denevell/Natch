package org.denevell.natch.serv.users;

import java.io.IOException;
import java.util.ResourceBundle;

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
import org.denevell.natch.db.entities.UserEntity;
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
	private CallDbBuilder<UserEntity> mModel;
	private CallDbBuilder<UserEntity> mUserListModel;
	
	public PasswordResetRequest() {
		mModel = new CallDbBuilder<UserEntity>();
		mUserListModel = new CallDbBuilder<UserEntity>()
		 .namedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME);
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
	@Path("/{username}")
	public void requestReset(@PathParam("username") @NotEmpty @NotBlank String username) throws IOException {
	    UserEntity user = mUserListModel.queryParam("username", username).single(UserEntity.class);  	    
	    if(user==null) {
	    	mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
	    	return;
	    }
		user.setPasswordResetRequest(true);
		mModel.update(user);
	}	

	@DELETE
	@Path("remove/{username}")
	public void requestNoReset(@PathParam("username") @NotEmpty @NotBlank String username) throws IOException {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if(!userEntity.isAdmin()) {
			mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
	    UserEntity user = mUserListModel.queryParam("username", username).single(UserEntity.class);  	    
		user.setPasswordResetRequest(false);
		mModel.update(user);
	}	
	
}

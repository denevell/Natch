package org.denevell.natch.serv.users;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.CallDbBuilder.RunnableWith;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.base.SuccessOrError;
import org.denevell.natch.utils.Strings;


@Path("user/admin/toggle")
public class UsersAdminToggleRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Context ServletContext context;
	private CallDbBuilder<UserEntity> mModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginAuthKeysSingleton mAuthDataGenerator;
	
	public UsersAdminToggleRequest() {
        mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
        mModel = new CallDbBuilder<UserEntity>();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public UsersAdminToggleRequest(CallDbBuilder<UserEntity> userModel, HttpServletRequest request) {
		mModel = userModel;
		mRequest = request;
	}
	
	@POST
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public SuccessOrError toggleAdmin(@PathParam("userId") final String userId) throws IOException {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if (!userEntity.isAdmin()) {
			mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		} else {
			boolean found = mModel
	        		.queryParam("username", userId)
	        		.findAndUpdate(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, new RunnableWith<UserEntity>() {
	        			@Override public void item(UserEntity item) {
	        				boolean admin = !item.isAdmin();
	        				item.setAdmin(admin);
	        				UserEntity loggedInEntity = mAuthDataGenerator.getLoggedinUser(userId);
	        				if(loggedInEntity!=null) {
	        					loggedInEntity.setAdmin(admin);
	        				}
	        			}
	        		}, UserEntity.class);
			if(!found) {
				mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			} else {
				SuccessOrError successOrError = new SuccessOrError();
				successOrError.setSuccessful(found);
				return successOrError;
			}
		}
	}
}

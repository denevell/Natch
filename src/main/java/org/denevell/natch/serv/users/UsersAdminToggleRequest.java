package org.denevell.natch.serv.users;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.inject.Inject;
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

import org.denevell.natch.io.base.SuccessOrError;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserAdminToggleModel;
import org.denevell.natch.model.interfaces.UserGetLoggedInModel;
import org.denevell.natch.utils.Strings;


@Path("user/admin/toggle")
public class UsersAdminToggleRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Context ServletContext context;
	@Inject UserAdminToggleModel mModel;
	@Inject UserGetLoggedInModel mUserLogggedInModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public UsersAdminToggleRequest() {
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public UsersAdminToggleRequest(HttpServletRequest request) {
		mRequest = request;
	}
	
	@POST
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public SuccessOrError toggleAdmin(@PathParam("userId") final String userId) throws IOException {
		UserEntity userEntity = mUserLogggedInModel.get(mRequest);
		if (!userEntity.isAdmin()) {
			mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		} else {
			int result = mModel.toggleAdmin(userId);
			if(result==UserAdminToggleModel.CANT_FIND) {
				mResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			} else {
				SuccessOrError successOrError = new SuccessOrError();
				successOrError.setSuccessful(true);
				return successOrError;
			}
		}
	}
}

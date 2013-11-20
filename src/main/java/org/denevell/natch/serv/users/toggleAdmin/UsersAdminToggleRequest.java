package org.denevell.natch.serv.users.toggleAdmin;

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

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.io.base.SuccessOrError;
import org.denevell.natch.utils.Strings;


@Path("user/admin/toggle")
public class UsersAdminToggleRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Context ServletContext context;
	private UsersAdminToggleModel mModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public UsersAdminToggleRequest() {
		mModel = new UsersAdminToggleModel();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public UsersAdminToggleRequest(UsersAdminToggleModel userModel, HttpServletRequest request, UserEntityQueries userEntites) {
		mModel = userModel;
		mRequest = request;
	}
	
	@POST
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public SuccessOrError toggleAdmin(@PathParam("userId") String userId) throws IOException {
		try {
			mModel.init();
            UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
            if(!userEntity.isAdmin()) {
                mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED); 
                return null;
            } else {
                String error = mModel.toggleAdmin(userId);
                SuccessOrError successOrError = new SuccessOrError();
                successOrError.setError(error);
                successOrError.setSuccessful(error==null);
                return successOrError;
            }
		} finally {
			mModel.close();
		}
	}
}

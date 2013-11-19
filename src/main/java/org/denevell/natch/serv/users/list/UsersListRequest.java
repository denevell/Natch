package org.denevell.natch.serv.users.list;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.users.User;
import org.denevell.natch.io.users.UserList;
import org.denevell.natch.utils.Strings;

import com.wordnik.swagger.annotations.Api;


@Path("user/list")
@Api(value="/user", description="Register, login, logout and see if a user is logged in.")
public class UsersListRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Context ServletContext context;
	private UsersListModel mLoginModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public UsersListRequest() {
		mLoginModel = new UsersListModel();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public UsersListRequest(UsersListModel userModel, HttpServletRequest request) {
		mLoginModel = userModel;
		mRequest = request;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserList listUsers() throws IOException {
		try {
			mLoginModel.init();
            UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
            if(!userEntity.isAdmin()) {
                mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED); 
                return null;
            } else {
                List<UserEntity> usersFromDb = mLoginModel.listUsers();
                UserList usersList = new UserList();
                for (UserEntity user: usersFromDb) {
                   User u = new User(user.getUsername(), user.isAdmin()); 
                   usersList.getUsers().add(u);
                }
                return usersList;
            }
		} finally {
			mLoginModel.close();
		}
	}


}

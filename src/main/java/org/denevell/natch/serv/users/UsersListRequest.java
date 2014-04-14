package org.denevell.natch.serv.users;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;
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
import org.denevell.natch.io.users.User;
import org.denevell.natch.io.users.UserList;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UsersListModel;
import org.denevell.natch.utils.Strings;

@Path("user/list")
public class UsersListRequest {

	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Context ServletContext context;
	@Inject UsersListModel mUserList;
	ResourceBundle rb = Strings.getMainResourceBundle();

	public UsersListRequest() {
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserList listUsers() throws IOException {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		if (!userEntity.isAdmin()) {
			mResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		} else {
			UserList usersList = new UserList();
			List<UserEntity> usersFromDb = mUserList.list(0, 1000);
			for (UserEntity user : usersFromDb) {
				User u = new User(user.getUsername(), user.isAdmin());
				u.setResetPasswordRequest(user.isPasswordResetRequest());
				u.setRecoveryEmail(user.getRecoveryEmail());
				usersList.getUsers().add(u);
			}
			return usersList;
		}
	}

}

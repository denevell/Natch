package org.denevell.natch.serv.users;

import java.util.ResourceBundle;

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
import org.denevell.natch.db.CallDbBuilder.RunnableWith;
import org.denevell.natch.io.users.ChangePasswordInput;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.utils.Strings;

@Path("user/password")
public class ChangePasswordRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context HttpServletResponse mResponse;
	@Context ServletContext context;
	private CallDbBuilder<UserEntity> mModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public ChangePasswordRequest() {
        mModel = new CallDbBuilder<UserEntity>();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public ChangePasswordRequest(CallDbBuilder<UserEntity> userModel, HttpServletRequest request) {
		mModel = userModel;
		mRequest = request;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void changePassword(@Valid final ChangePasswordInput changePass) throws Exception {
		final UserEntity userEntity = LoginHeadersFilter.getLoggedInUser(mRequest);
		boolean found = mModel
			.startTransaction()
			.queryParam("username", userEntity.getUsername())
			.findAndUpdate(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME,
				new RunnableWith<UserEntity>() {
					@Override
					public void item(UserEntity item) {
						item.generatePassword(changePass.getPassword());
					}
				}, 
				UserEntity.class);
		mModel.commitAndCloseEntityManager();
		if(!found) mResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
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
		boolean found = mModel
			.startTransaction()
			.queryParam("username", username)
			.findAndUpdate(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME,
				new RunnableWith<UserEntity>() {
					@Override
					public void item(UserEntity item) {
						item.generatePassword(changePass.getPassword());
					}
				}, 
				UserEntity.class);
		mModel.commitAndCloseEntityManager();
		if(!found) mResponse.sendError(HttpServletResponse.SC_NOT_FOUND); 
	}
}

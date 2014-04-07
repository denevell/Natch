package org.denevell.natch.serv.users;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.CallDbBuilder.RunnableWith;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;


@Path("user")
public class RegisterRequest {
	
	@Context UriInfo info;
	@Context HttpServletRequest mRequest;
	@Context ServletContext context;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private CallDbBuilder<UserEntity> mModel;
	
	public RegisterRequest() {
		mModel = new CallDbBuilder<UserEntity>();
	}
	
	/**
	 * For DI testing.
	 * @param request 
	 */
	public RegisterRequest(CallDbBuilder<UserEntity> userModel, HttpServletRequest request) {
		mModel = userModel;
		mRequest = request;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RegisterResourceReturnData register(@Valid RegisterResourceInput registerInput) {
		UserEntity u = new UserEntity(registerInput);
		boolean added = mModel
			.startTransaction()
			.ifFirstItem(UserEntity.NAMED_QUERY_COUNT, new RunnableWith<UserEntity>() {
						@Override public void item(UserEntity item) {
							item.setAdmin(true);
						}
					})
			.queryParam("username", u.getUsername())
			.addIfDoesntExist(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, u);

		RegisterResourceReturnData regReturnData = new RegisterResourceReturnData();
		if (added) {
			regReturnData.setSuccessful(true);
		} else if (!added) {
			regReturnData.setSuccessful(false);
			regReturnData.setError(rb.getString(Strings.username_already_exists));
		} 
		return regReturnData;
	}	
	
}

package org.denevell.natch.serv.users.register;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.UserEntity;

public class RegisterModel {
	
	public static String USER_INPUT_ERROR = "inputError";
	public static String REGISTERED="registered";
	public static String DUPLICATE_USERNAME="dupusername";
	private CallDbBuilder<UserEntity> mModel;
	
	/**
	 * For DI testing
	 */
	public RegisterModel(CallDbBuilder<UserEntity> model) {
		mModel = model;
	}
	
	public RegisterModel() {
		mModel = new CallDbBuilder<UserEntity>();
	}

	public String addUserToSystem(String username, String password) {
		if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
			return USER_INPUT_ERROR;
		}
		UserEntity u = new UserEntity();
		u.generatePassword(password);
		u.setUsername(username);
		if(mModel.namedQuery(UserEntity.NAMED_QUERY_COUNT).isFirst()) {
			u.setAdmin(true);
		}
		if(mModel
				.queryParam("username", username)
				.addIfDoesntExist(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, u)) {
			return REGISTERED;
		} else {
			return DUPLICATE_USERNAME;
		}
	}	

}
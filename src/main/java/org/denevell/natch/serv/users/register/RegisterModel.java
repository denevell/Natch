package org.denevell.natch.serv.users.register;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.PasswordSaltUtils;

public class RegisterModel {
	
	private PasswordSaltUtils mPasswordSalter;
	public static String USER_INPUT_ERROR = "inputError";
	public static String UNKNOWN_ERROR = "unknownError";
	public static String REGISTERED="registered";
	public static String DUPLICATE_USERNAME="dupusername";
	private CallDbBuilder<UserEntity> mModel;
	
	/**
	 * For DI testing
	 */
	public RegisterModel(
			CallDbBuilder<UserEntity> model,
	        PasswordSaltUtils saltUtils) {
		mModel = model;
		mPasswordSalter = saltUtils;
	}
	
	public RegisterModel() {
		mModel = new CallDbBuilder<UserEntity>();
		mPasswordSalter = new PasswordSaltUtils();
	}

	public String addUserToSystem(String username, String password) {
		try {
			if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
				return USER_INPUT_ERROR;
			}
			UserEntity u = new UserEntity();
			password = mPasswordSalter.generatedSaltedPassword(password);
			u.setPassword(password);
			u.setUsername(username);
			if(mModel.namedQuery(UserEntity.NAMED_QUERY_COUNT)
					.isFirst()) {
				u.setAdmin(true);
			}
			if(!mModel
					.namedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME)
					.queryParam("username", username)
					.exists()) {
				mModel.add(u);
				return REGISTERED;
			} else {
				return DUPLICATE_USERNAME;
			}
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			return UNKNOWN_ERROR;
		} 
	}	

}
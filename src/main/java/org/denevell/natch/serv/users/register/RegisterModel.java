package org.denevell.natch.serv.users.register;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.CallDbBuilder.RunnableWith;
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

	public String addUserToSystem(UserEntity u) {
		if(u.getOriginalPassword()==null || u.getOriginalPassword().trim().length()==0 || 
				u.getUsername()==null || u.getUsername().trim().length()==0) {
			return USER_INPUT_ERROR;
		}
		if(mModel
				.queryParam("username", u.getUsername())
				.ifFirstItem(UserEntity.NAMED_QUERY_COUNT, new RunnableWith<UserEntity>() {
							@Override public void item(UserEntity item) {
								item.setAdmin(true);
							}
						})
				.addIfDoesntExist(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, u)) {
			return REGISTERED;
		} else {
			return DUPLICATE_USERNAME;
		}
	}	

}
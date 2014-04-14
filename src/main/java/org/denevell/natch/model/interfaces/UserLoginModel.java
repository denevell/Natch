package org.denevell.natch.model.interfaces;

import org.denevell.natch.model.entities.UserEntity;

public interface UserLoginModel {

	public static class UserEntityAndAuthKey {
		public UserEntityAndAuthKey(UserEntity userEntity, String authKey) {
			this.authKey = authKey;
			this.userEntity = userEntity;
		}
		public String authKey;
		public UserEntity userEntity; 
	}
	/**
	 * @return The auth key
	 */
	UserEntityAndAuthKey login(String username, String password);

}

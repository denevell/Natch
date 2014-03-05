package org.denevell.natch.serv.users.logout;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;

public class LogoutModel {
	
	private LoginAuthKeysSingleton mAuthDataGenerator;

	/**
	 * For DI testing
	 */
	public LogoutModel(LoginAuthKeysSingleton authKeyGenerator) {
		mAuthDataGenerator = authKeyGenerator;
	}
	
	public LogoutModel() {
		mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
	}
	
	public boolean logout(String authKey) {
		if(authKey==null || authKey.trim().length()==0) {
			return false;
		}
		mAuthDataGenerator.remove(authKey);
		UserEntity username = mAuthDataGenerator.retrieveUserEntity(authKey);
		if(username!=null) {
			return false;
		} else {
			return true;
		}
	}	


}
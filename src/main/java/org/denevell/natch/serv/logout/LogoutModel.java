package org.denevell.natch.serv.logout;

import org.denevell.natch.auth.LoginAuthKeysSingleton;

public class LogoutModel {
	
	private LoginAuthKeysSingleton mAuthDataGenerator;
	
	/**
	 * For DI testing
	 * @param authKeyGenerator 
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
		String username = mAuthDataGenerator.retrieveUsername(authKey);
		if(username!=null) {
			return false;
		} else {
			return true;
		}
	}

}


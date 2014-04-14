package org.denevell.natch.model.impl;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserLogoutModel;

public class UserLogoutModelImpl implements UserLogoutModel {

	private LoginAuthKeysSingleton mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
	
	@Override
	public int logout(String authKey) {
		if(authKey!=null && authKey.trim().length()!=0) {
			mAuthDataGenerator.remove(authKey);
			UserEntity username = mAuthDataGenerator.retrieveUserEntity(authKey);
			if(username == null) {
				return UserLogoutModel.SUCCESS;
			} 
			return UserLogoutModel.FAIL;
		}
		return UserLogoutModel.FAIL;
	}

}

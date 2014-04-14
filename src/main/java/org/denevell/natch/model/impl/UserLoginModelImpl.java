package org.denevell.natch.model.impl;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserLoginModel;
import org.denevell.natch.utils.PasswordSaltUtils;

public class UserLoginModelImpl implements UserLoginModel {

	private CallDbBuilder<UserEntity> mLoginModel = new CallDbBuilder<UserEntity>();
	private LoginAuthKeysSingleton mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
	private PasswordSaltUtils mSaltedPasswordUtils = new PasswordSaltUtils();
	
	@Override
	public UserEntityAndAuthKey login(String username, String password) {
		UserEntity res = mLoginModel
				.startTransaction()
				.namedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME)
				.queryParam("username", username)
				.single(UserEntity.class);
		mLoginModel.commitAndCloseEntityManager();
		if(res==null) {
            return null;
		}
		if (mSaltedPasswordUtils.checkSaltedPassword(password, res.getPassword())) {
			return new UserEntityAndAuthKey(res, mAuthDataGenerator.generate(res));
		} else {
            return null;
		}
	}

}
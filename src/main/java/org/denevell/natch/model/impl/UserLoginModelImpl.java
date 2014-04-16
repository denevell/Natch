package org.denevell.natch.model.impl;

import javax.inject.Singleton;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserLoginModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.PasswordSaltUtils;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class UserLoginModelImpl implements UserLoginModel {

	private Jrappy<UserEntity> mLoginModel = new Jrappy<UserEntity>(JPAFactoryContextListener.sFactory);
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

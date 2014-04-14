package org.denevell.natch.model.impl;

import javax.inject.Singleton;
import javax.servlet.ServletRequest;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserGetLoggedInModel;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class UserGetLoggedInModelImpl implements UserGetLoggedInModel {

	@Override
	public UserEntity get(Object authObject) {
		UserEntity userEntity = LoginHeadersFilter.getLoggedInUser((ServletRequest) authObject);
		return userEntity;
	}

}

package org.denevell.natch.model.impl;

import org.denevell.jrappy.Jrappy;
import org.denevell.jrappy.Jrappy.RunnableWith;
import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserAdminToggleModel;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class UserAdminToggleModelImpl implements UserAdminToggleModel {
	
	private Jrappy<UserEntity> mModel = new Jrappy<UserEntity>(JPAFactoryContextListener.sFactory);
	private LoginAuthKeysSingleton mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
	
	@Override
	public int toggleAdmin(final String userId) {
		boolean found = mModel
				.startTransaction()
        		.queryParam("username", userId)
        		.findAndUpdate(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, new RunnableWith<UserEntity>() {
        			@Override public void item(UserEntity item) {
        				boolean admin = !item.isAdmin();
        				item.setAdmin(admin);
        				UserEntity loggedInEntity = mAuthDataGenerator.getLoggedinUser(userId);
        				if(loggedInEntity!=null) {
        					loggedInEntity.setAdmin(admin);
        				}
        			}
        		}, UserEntity.class);
		mModel.commitAndCloseEntityManager();
		if(found) {
			return UserAdminToggleModel.TOGGLED;
		} else {
			return UserAdminToggleModel.CANT_FIND;
		}
	}

}

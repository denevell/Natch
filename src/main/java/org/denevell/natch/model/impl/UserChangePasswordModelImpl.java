package org.denevell.natch.model.impl;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.CallDbBuilder.RunnableWith;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserChangePasswordModel;

public class UserChangePasswordModelImpl implements UserChangePasswordModel {
	private CallDbBuilder<UserEntity> mModel = new CallDbBuilder<UserEntity>();
	
	@Override
	public int changePassword(
			final String username, 
			final String password) {
		boolean found = mModel
			.startTransaction()
			.queryParam("username", username)
			.findAndUpdate(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME,
				new RunnableWith<UserEntity>() {
					@Override
					public void item(UserEntity item) {
						item.generatePassword(password);
					}
				}, 
				UserEntity.class);
		mModel.commitAndCloseEntityManager();
		if(found) {
			return UserChangePasswordModel.CHANGED;
		} else {
			return UserChangePasswordModel.NOT_FOUND;
		}
	}

}

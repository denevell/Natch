package org.denevell.natch.model.impl;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.CallDbBuilder.RunnableWith;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserAddModel;

public class UserAddModelImpl implements UserAddModel {
	
	private CallDbBuilder<UserEntity> mModel = new CallDbBuilder<UserEntity>();

	public int add(final UserEntity user) {
		boolean exists = mModel
				.startTransaction()
				.namedQuery(UserEntity.NAMED_QUERY_FIND_BY_RECOVERY_EMAIL)
				.queryParam("recoveryEmail", user.getRecoveryEmail())
				.exists();
			if(exists) {
				return UserAddModel.EMAIL_ALREADY_EXISTS;
			}
			boolean added = mModel
				.clearQueryParams()
				.useTransaction(mModel.getEntityManager())
				.ifFirstItem(UserEntity.NAMED_QUERY_COUNT, new RunnableWith<UserEntity>() {
							@Override public void item(UserEntity item) {
								item.setAdmin(true);
							}
						})
				.queryParam("username", user.getUsername())
				.addIfDoesntExist(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, user);
			mModel.commitAndCloseEntityManager();
			if(!added) {
				return UserAddModel.USER_ALREADY_EXISTS;
			} else {
				return UserAddModel.ADDED;
			}
	}

}

package org.denevell.natch.model.impl;

import java.util.List;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UsersListModel;

public class UsersListModelImpl implements UsersListModel {

	private CallDbBuilder<UserEntity> mModel = new CallDbBuilder<UserEntity>();
	
	@Override
	public List<UserEntity> list(int start, int limit) {
		List<UserEntity> usersFromDb = mModel
				.startTransaction()
				.namedQuery(UserEntity.NAMED_QUERY_LIST_USERS).list(
						UserEntity.class);
		mModel.commitAndCloseEntityManager();
		return usersFromDb;
	}

}

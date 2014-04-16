package org.denevell.natch.model.impl;

import java.util.List;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UsersListModel;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class UsersListModelImpl implements UsersListModel {

	private Jrappy<UserEntity> mModel = new Jrappy<UserEntity>(JPAFactoryContextListener.sFactory);
	
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

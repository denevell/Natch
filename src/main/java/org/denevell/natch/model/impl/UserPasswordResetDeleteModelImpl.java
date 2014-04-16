package org.denevell.natch.model.impl;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserPasswordResetDeleteModel;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class UserPasswordResetDeleteModelImpl implements UserPasswordResetDeleteModel {

	private Jrappy<UserEntity> mModel = new Jrappy<UserEntity>(JPAFactoryContextListener.sFactory)
		 .namedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME);
	
	@Override
	public int deleteRequest(String username) {
	    UserEntity user = mModel
	    		.startTransaction()
	    		.queryParam("username", username)
	    		.single(UserEntity.class);  	    
	    if(user==null) {
	    	return UserPasswordResetDeleteModel.CANT_FIND;
	    }
		user.setPasswordResetRequest(false);
		mModel
			.useTransaction(mModel.getEntityManager())
			.update(user);
		mModel.commitAndCloseEntityManager();
		return UserPasswordResetDeleteModel.UPDATED;
	}

}

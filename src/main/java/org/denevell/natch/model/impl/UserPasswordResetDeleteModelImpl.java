package org.denevell.natch.model.impl;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserPasswordResetDeleteModel;

public class UserPasswordResetDeleteModelImpl implements UserPasswordResetDeleteModel {

	private CallDbBuilder<UserEntity> mModel = new CallDbBuilder<UserEntity>()
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

package org.denevell.natch.model.impl;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserPasswordResetRequestModel;

public class UserPasswordResetRequestModelImpl implements UserPasswordResetRequestModel {
	
	CallDbBuilder<UserEntity> mModel = new CallDbBuilder<UserEntity>();
	
	@Override
	public int requestReset(String recoveryEmail) {
	    UserEntity user = mModel 
	    		.namedQuery(UserEntity.NAMED_QUERY_FIND_BY_RECOVERY_EMAIL)
	    		.startTransaction()
	    		.queryParam("recoveryEmail", recoveryEmail).single(UserEntity.class);  	    
	    if(user==null) {
	    	return UserPasswordResetRequestModel.EMAIL_NOT_FOUND;
	    }
		user.setPasswordResetRequest(true);
		mModel
			.useTransaction(mModel.getEntityManager())
			.update(user)
			.commitAndCloseEntityManager();
	    return UserPasswordResetRequestModel.REQUESTED;
	}

}

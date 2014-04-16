package org.denevell.natch.model.impl;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.UserPasswordResetRequestModel;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class UserPasswordResetRequestModelImpl implements UserPasswordResetRequestModel {
	
	Jrappy<UserEntity> mModel = new Jrappy<UserEntity>(JPAFactoryContextListener.sFactory);
	
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

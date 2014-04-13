package org.denevell.natch.model.impl;

import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.model.interfaces.GetTransaction;
import org.denevell.natch.model.interfaces.PostFindModel;
import org.denevell.natch.model.interfaces.ReuseTransaction;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PostFindModelImpl implements PostFindModel, ReuseTransaction, GetTransaction {

	private CallDbBuilder<PostEntity> mPostModel = new CallDbBuilder<PostEntity>();
	private EntityManager mExistingEntityManager;
	
	/**
	 * Doesn't close the entity manager
	 */
	public PostEntity find(long id, boolean pessmesticRead) {
		if(mExistingEntityManager==null) {
			mPostModel.startTransaction();
			mExistingEntityManager = mPostModel.getEntityManager();
		} else {
			mPostModel.useTransaction(mExistingEntityManager);
		}
		PostEntity f = mPostModel.find(id, pessmesticRead, PostEntity.class);
		return f;
	}

	@Override
	public void setExistingTransactionObject(Object em) {
		mExistingEntityManager = (EntityManager) em;
	}

	@Override
	public Object getTransactionObject() {
		return mExistingEntityManager;
	}
}
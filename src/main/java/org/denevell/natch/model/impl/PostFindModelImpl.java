package org.denevell.natch.model.impl;

import javax.inject.Singleton;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.interfaces.PostFindModel;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PostFindModelImpl implements PostFindModel {

	private CallDbBuilder<PostEntity> mPostModel = new CallDbBuilder<PostEntity>();
	
	public PostEntity find(long id, boolean pessmesticRead) {
		PostEntity f = mPostModel.find(id, pessmesticRead, PostEntity.class);
		return f;
	}

}
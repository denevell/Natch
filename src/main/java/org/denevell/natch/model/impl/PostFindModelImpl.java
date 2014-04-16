package org.denevell.natch.model.impl;

import javax.inject.Singleton;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.interfaces.PostFindModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PostFindModelImpl implements PostFindModel {

	private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);
	
	public PostEntity find(long id, boolean pessmesticRead) {
		PostEntity f = mPostModel.find(id, pessmesticRead, PostEntity.class);
		return f;
	}

}
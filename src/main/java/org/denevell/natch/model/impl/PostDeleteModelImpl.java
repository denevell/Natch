package org.denevell.natch.model.impl;

import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.denevell.jrappy.Jrappy;
import org.denevell.jrappy.Jrappy.DeleteOrMerge;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.PostDeleteModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service
public class PostDeleteModelImpl implements PostDeleteModel { 

	private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory); 
	private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);
	/**
	 * Doesn't close the entity manager
	 */
	public int delete(long id, String username, boolean adminEditing) {
		final PostEntity pe = mPostModel
				.startTransaction()
				.find(id, false, PostEntity.class);
		EntityManager postEntityManager = mPostModel.getEntityManager();
		if(pe==null) {
			mPostModel.commitAndCloseEntityManager();
			return PostDeleteModel.DOESNT_EXIST;
		} else if(!adminEditing && !pe.getUsername().equals(username)) {
			mPostModel.commitAndCloseEntityManager();
			return PostDeleteModel.NOT_YOURS;
		}
		mThreadModel.useTransaction(postEntityManager)
			.findAndUpdateOrDelete(pe.getThreadId(), 
				new DeleteOrMerge<ThreadEntity>() {
					@Override public boolean shouldDelete(ThreadEntity item) {
						item.updateThreadToRemovePost(pe);
						return item.getPosts()==null || item.getPosts().size()==0;
					}
				}, 
				ThreadEntity.class);
		postEntityManager.remove(pe);
		mThreadModel.commitAndCloseEntityManager();
		return PostDeleteModel.DELETED;
	}

}
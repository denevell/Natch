package org.denevell.natch.model.impl;

import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.CallDbBuilder.DeleteOrMerge;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.model.interfaces.PostDeleteModel;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PostDeleteModelImpl implements PostDeleteModel { 

	private CallDbBuilder<PostEntity> mPostModel = new CallDbBuilder<PostEntity>(); 
	private CallDbBuilder<ThreadEntity> mThreadModel = new CallDbBuilder<ThreadEntity>();
	/**
	 * Doesn't close the entity manager
	 */
	public int delete(long id, UserEntity userEntity) {
		final PostEntity pe = mPostModel
				.startTransaction()
				.find(id, false, PostEntity.class);
		EntityManager postEntityManager = mPostModel.getEntityManager();
		if(pe==null) {
			mPostModel.commitAndCloseEntityManager();
			return PostDeleteModel.DOESNT_EXIST;
		} else if(!userEntity.isAdmin() && !pe.getUser().getUsername().equals(userEntity.getUsername())) {
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
package org.denevell.natch.serv.post.delete;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.CallDbBuilder.DeleteOrMerge;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class DeletePostModel {

	public final static String DELETED = "deleted";
	public final static String DOESNT_EXIST = "doesntexist";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String NOT_YOURS_TO_DELETE = "notyourtodelete";
	private EntityManager mEntityManager;
	private CallDbBuilder<ThreadEntity> mThreadModel;
	private CallDbBuilder<PostEntity> mPostModel;
	
	public DeletePostModel() {
		mThreadModel = new CallDbBuilder<ThreadEntity>();
		mPostModel = new CallDbBuilder<PostEntity>();
	}
	
	public void init() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager(); 
	}
	
	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager); 
	}
	
	/**
	 * For testing / di
	 */
	public DeletePostModel(EntityManagerFactory factory, EntityManager entityManager) {
		mEntityManager = entityManager;
	}

	public String delete(UserEntity userEntity, long postEntityId) {
			final PostEntity pe = mPostModel.find(postEntityId, false, mEntityManager, PostEntity.class);
			if(pe==null) {
				return DOESNT_EXIST;
			} else if(!userEntity.isAdmin() && !pe.getUser().getUsername().equals(userEntity.getUsername())) {
				return NOT_YOURS_TO_DELETE;
			}
			mEntityManager.getTransaction().begin();
			mThreadModel
				.useTransaction(mEntityManager)
				.findAndUpdateOrDelete(
						pe.getThreadId(), 
						new DeleteOrMerge<ThreadEntity>() {
							@Override public boolean shouldDelete(ThreadEntity item) {
								item.updateThreadToRemovePost(pe);
								return item.getPosts()==null || item.getPosts().size()==0;
							}
						}, 
						ThreadEntity.class);
			mEntityManager.remove(pe);
			mThreadModel.commitAndCloseEntityManager();
			return DELETED;
	}
	
}
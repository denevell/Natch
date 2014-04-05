package org.denevell.natch.serv.post.delete;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;

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

	/**
	 * 1. Check if the posts exists
	 * 2. Check if it's the users or the user is an admin
	 * 3. Find the thread and update to remove post
	 * 4. Either update the thread or remove it
	 * 5. Remove the post
	 * @return
	 */
	public String delete(UserEntity userEntity, long postEntityId) {
		EntityTransaction trans = mEntityManager.getTransaction();
		try {
			PostEntity pe = mPostModel.find(postEntityId, false, mEntityManager, PostEntity.class);
			if(pe==null) {
				return DOESNT_EXIST;
			} else if(!userEntity.isAdmin() && !pe.getUser().getUsername().equals(userEntity.getUsername())) {
				return NOT_YOURS_TO_DELETE;
			}
			trans.begin();
			ThreadEntity th = mThreadModel.find(pe.getThreadId(), true, mEntityManager, ThreadEntity.class);
			th = updateThreadToRemovePost(th, pe);
			// Remote thread if needs be
			if(th.getPosts()==null || th.getPosts().size()==0) {
				mEntityManager.remove(th);
			} else {
				mEntityManager.merge(th);
			}
			mEntityManager.remove(pe);
			trans.commit();
			return DELETED;
		} catch(Exception e) {
			Log.info(getClass(), "Error deleting: " + e.toString());
			e.printStackTrace();
			try {
				trans.rollback();
			} catch(Exception e1) {
				Log.info(getClass(), "Error rolling back: " + e.toString());
				e1.printStackTrace();
			}
			return UNKNOWN_ERROR;
		} 
	}
	
	private ThreadEntity updateThreadToRemovePost(ThreadEntity th, PostEntity pe) {
		th.getPosts().remove(pe);
		if(th.getRootPost()!=null && th.getRootPost().getId()==pe.getId()) {
			th.setRootPost(null);
		}
		if(th.getLatestPost()!=null && th.getLatestPost().getId()==pe.getId() && th.getPosts()!=null && th.getPosts().size()>=1) {
			th.setLatestPost(th.getPosts().get(th.getPosts().size()-1));
		}
		th.setNumPosts(th.getNumPosts()-1);
		return th;
	}	

}
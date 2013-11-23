package org.denevell.natch.serv.post.delete;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

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
	
	public DeletePostModel() {
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

	
	public PostEntity findPostById(long id) {
		try {
			TypedQuery<PostEntity> q = mEntityManager
					.createNamedQuery(PostEntity.NAMED_QUERY_FIND_BY_ID, PostEntity.class);
			q.setParameter(PostEntity.NAMED_QUERY_PARAM_ID, id);
			List<PostEntity> resultList = q.getResultList();		
			if(resultList==null || resultList.size()==0) return null;
			else return resultList.get(0);
		} catch(Exception e) {
			Log.info(getClass(), "Error finding post by id: " + e.toString());
			return null;
		}
	}
	
	public ThreadEntity findThreadById(String id) {
		try {
			TypedQuery<ThreadEntity> q = mEntityManager
					.createNamedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID, ThreadEntity.class);
			q.setParameter(ThreadEntity.NAMED_QUERY_PARAM_ID, id);
			List<ThreadEntity> resultList = q.getResultList();		
			if(resultList==null || resultList.size()==0) return null;
			else return resultList.get(0);
		} catch(Exception e) {
			Log.info(getClass(), "Error finding thread by id: " + e.toString());
			return null;
		} 
	}	
	
	public String delete(UserEntity userEntity, long postEntityId) {
		EntityTransaction trans = mEntityManager.getTransaction();
		try {
			if(userEntity==null) {
				Log.info(getClass(), "No user passed to delete method.");
				return UNKNOWN_ERROR;
			}
			PostEntity pe = findPostById(postEntityId);
			if(pe==null) {
				return DOESNT_EXIST;
			} else if(!userEntity.isAdmin() && !pe.getUser().getUsername().equals(userEntity.getUsername())) {
				return NOT_YOURS_TO_DELETE;
			}
			ThreadEntity th = findThreadById(pe.getThreadId());
			th = updateThreadToRemovePost(th, pe);
			trans.begin();
			// Remote thread if needs be
			ThreadEntity thm = null;
			if(th.getPosts()==null || th.getPosts().size()==0) {
				mEntityManager.remove(th);
			} else {
				thm = mEntityManager.merge(th);
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
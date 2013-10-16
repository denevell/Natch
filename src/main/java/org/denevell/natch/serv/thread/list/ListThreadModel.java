package org.denevell.natch.serv.thread.list;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;

public class ListThreadModel {

	public final static String EDITED = "edited";
	public final static String DELETED = "deleted";
	public final static String ADDED = "added";
	public final static String DOESNT_EXIST = "doesntexist";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	public final static String NOT_YOURS_TO_DELETE = "notyourtodelete";
	private EntityManager mEntityManager;
	
	public ListThreadModel() {
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
	public ListThreadModel(EntityManagerFactory factory, EntityManager entityManager) {
		mEntityManager = entityManager;
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
		
		public List<PostEntity> listByThreadId(String threadId, int startPos, int limit) {
			TypedQuery<PostEntity> q = mEntityManager.createNamedQuery(PostEntity.NAMED_QUERY_FIND_BY_THREADID, PostEntity.class);
			if(startPos<0) startPos=0; if(limit<0) limit=0;
			q.setFirstResult(startPos);
			q.setMaxResults(limit);
			q.setParameter(PostEntity.NAMED_QUERY_PARAM_THREADID, threadId);
			List<PostEntity> resultList = q.getResultList();		
			return resultList;
		}	
		
}
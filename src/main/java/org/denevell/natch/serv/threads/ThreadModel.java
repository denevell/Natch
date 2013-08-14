package org.denevell.natch.serv.threads;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.ThreadFactory;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;

public class ThreadModel {

	public final static String EDITED = "edited";
	public final static String DELETED = "deleted";
	public final static String DOESNT_EXIST = "doesntexist";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	public final static String NOT_YOURS_TO_DELETE = "notyourtodelete";
	private EntityManager mEntityManager;
	private ThreadFactory mThreadFactory;
	
	public ThreadModel() {
	}
	
	public void init() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager(); 
		mThreadFactory = new ThreadFactory();
	}
	
	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager); 
	}
	
	/**
	 * For testing / di
	 */
	public ThreadModel(EntityManagerFactory factory, EntityManager entityManager, ThreadFactory threadFactory) {
		mEntityManager = entityManager;
		mThreadFactory = threadFactory;
	}
	
	public String addThread(UserEntity user, String subject, String content, List<String> tags) {
		EntityTransaction trans = null;
		try {
			if(user==null || user.getUsername()==null || user.getUsername().isEmpty() ||
			   subject==null || content==null || 
			   subject.isEmpty() || content.isEmpty()) {
				Log.info(this.getClass(), "Bad user input");
				return BAD_USER_INPUT;
			}
			ThreadEntity thread = mThreadFactory.makeThread(subject, content, tags, user);
			trans = mEntityManager.getTransaction();
			trans.begin();
			mEntityManager.persist(thread);
			trans.commit();
			return String.valueOf(thread.getId());
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			if(trans!=null && trans.isActive()) trans.rollback();
			return UNKNOWN_ERROR;
		} 
	}	
		
	public List<ThreadEntity> listThreads(int startPos, int limit) {
		TypedQuery<ThreadEntity> q = mEntityManager.
				createNamedQuery(ThreadEntity.NAMED_QUERY_LIST_THREADS, ThreadEntity.class);		
		if(startPos<0) startPos=0; if(limit<0) limit=0;
		q.setFirstResult(startPos);
		q.setMaxResults(limit);
		List<ThreadEntity> resultList = q.getResultList();
		if(resultList==null) return new ArrayList<ThreadEntity>();
		else return resultList;
	}	
	
	public List<ThreadEntity> listThreadsByTag(String tag, int startPos, int limit) {
		TypedQuery<ThreadEntity> q = mEntityManager.
				createNamedQuery(ThreadEntity.NAMED_QUERY_LIST_THREADS_BY_TAG, ThreadEntity.class);		
		if(startPos<0) startPos=0; if(limit<0) limit=0;
		q.setFirstResult(startPos);
		q.setMaxResults(limit);
		q.setParameter(ThreadEntity.NAMED_QUERY_PARAM_TAG, tag);
		List<ThreadEntity> resultList = q.getResultList();
		if(resultList==null) return new ArrayList<ThreadEntity>();
		else return resultList;
	}	
}
	

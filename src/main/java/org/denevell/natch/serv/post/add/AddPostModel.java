package org.denevell.natch.serv.post.add;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.serv.posts.ThreadFactory;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;

public class AddPostModel {

	private EntityManager mEntityManager;
	private ThreadFactory mThreadFactory;
	
	public AddPostModel() {
		mThreadFactory = new ThreadFactory();
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
	public AddPostModel(EntityManagerFactory factory, EntityManager entityManager, ThreadFactory threadFactory) {
		mEntityManager = entityManager;
		mThreadFactory = threadFactory;
	}
	
	public ThreadEntity addPost(UserEntity user, AddPostResourceInput input) {
		EntityTransaction trans = null;
		try {
			long created = new Date().getTime();
			String threadId = null;
			if(input.getThreadId()==null || input.getThreadId().isEmpty()) {
				threadId = getThreadId(input.getSubject(), input.getThreadId(), created);
			} else {
				threadId = input.getThreadId();
			}
			PostEntity mPost = new PostEntity();
			mPost.setContent(input.getContent());
			mPost.setSubject(input.getSubject());
			mPost.setThreadId(threadId);
			mPost.setTags(input.getTags());			
			mPost.setUser(user);
			mPost.setCreated(created);
			mPost.setModified(created);			

			ThreadEntity thread = findThreadById(mPost.getThreadId());
			if(thread==null) {
				thread = mThreadFactory.makeThread(mPost);
			} else {
				thread = mThreadFactory.makeThread(thread, mPost);
			}
			trans = mEntityManager.getTransaction();
			trans.begin();
			mEntityManager.persist(thread);
			trans.commit();
			return thread;
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			if(trans!=null && trans.isActive()) trans.rollback();
			return null;
		} 
	}
	
	private String getThreadId(String subject, String threadId, long time) {
		if(threadId==null || threadId.trim().length()==0) {
			try {
				MessageDigest md5Algor = MessageDigest.getInstance("MD5");
				StringBuffer sb = new StringBuffer();
				byte[] digest = md5Algor.digest(subject.getBytes());
				for (byte b : digest) {
					sb.append(Integer.toHexString((int) (b & 0xff)));
				}				
				threadId = sb.toString();
			} catch (NoSuchAlgorithmException e) {
				Log.info(getClass(), "Couldn't get an MD5 hash. I guess we'll just use hashCode() then.");
				e.printStackTrace();
				threadId = String.valueOf(subject.hashCode());
			}
			threadId = threadId+String.valueOf(time);
		}
		return threadId;
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

}
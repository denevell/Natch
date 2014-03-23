package org.denevell.natch.serv.post.add;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.adapters.AddPostRequestToPostEntity;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
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
	public AddPostModel(EntityManagerFactory factory, 
	        EntityManager entityManager, 
	        ThreadFactory threadFactory) {
		mEntityManager = entityManager;
		mThreadFactory = threadFactory;
	}

	public ThreadEntity addPostAsDifferntUser(String userId, AddPostResourceInput input) {
		UserEntity user = new CallDbBuilder<UserEntity>()  
				.namedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME)
				.queryParam("username", userId)
				.single(UserEntity.class);		
	    return addPost(AddPostRequestToPostEntity.adapt(input, true, user));
	}

	public ThreadEntity addPost(UserEntity user, AddPostResourceInput input) {
	    return addPost(AddPostRequestToPostEntity.adapt(input, false, user));
	}
	
	public ThreadEntity addPost(PostEntity post) {
		EntityTransaction trans = null;
		try {
			trans = mEntityManager.getTransaction();
			trans.begin();
			ThreadEntity thread = findThreadById(post.getThreadId());
			if(thread==null) {
				thread = mThreadFactory.makeThread(post);
			} else {
				thread = mThreadFactory.makeThread(thread, post);
			}
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
	
	public ThreadEntity findThreadById(String id) {
		try {
		    ThreadEntity thread = mEntityManager.find(ThreadEntity.class, id, LockModeType.PESSIMISTIC_READ);
			return thread;
		} catch(Exception e) {
			Log.info(getClass(), "Error finding thread by id: " + e.toString());
			return null;
		} 
	}		

}
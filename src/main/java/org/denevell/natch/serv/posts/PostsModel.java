package org.denevell.natch.serv.posts;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.denevell.natch.db.entities.PersistenceInfo;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.Log;

public class PostsModel {

	public enum AddPostResult {
		ADDED, UNKNOWN_ERROR, BAD_USER_INPUT
	}

	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;
	private PostFactory mPostFactory;

	public PostsModel() {
		mFactory = Persistence.createEntityManagerFactory(PersistenceInfo.EntityManagerFactoryName);
		mEntityManager = mFactory.createEntityManager();		
		mPostFactory = new PostFactory();
	}
	
	/**
	 * For testing / di
	 */
	public PostsModel(EntityManagerFactory factory, EntityManager entityManager, PostFactory postFactory) {
		mFactory = factory;
		mEntityManager = entityManager;
		mPostFactory = postFactory;
	}
	
	public AddPostResult addPost(UserEntity user, String subject, String content) {
		PostEntity p = mPostFactory.createPost(user, subject, content);
		if(checkInputParams(user, subject, content) || p==null) {
			Log.info(this.getClass(), "Bad user input");
			return AddPostResult.BAD_USER_INPUT;
		}
		EntityTransaction trans = null;
		try {
			trans = mEntityManager.getTransaction();
			trans.begin();
			mEntityManager.persist(p);
			trans.commit();
			return AddPostResult.ADDED;
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			if(trans!=null && trans.isActive()) trans.rollback();
			return AddPostResult.UNKNOWN_ERROR;
		} finally {
			EntityUtils.closeEntityConnection(mFactory, mEntityManager);		
		}		
	}

	private boolean checkInputParams(UserEntity user, String subject, String content) {
		return  user==null || user.getUsername()==null || user.getUsername().trim().length()==0 ||
				subject==null || content==null || subject.trim().length()==0 || content.trim().length()==0;
	}


}
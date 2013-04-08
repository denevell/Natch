package org.denevell.natch.serv.posts;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.PersistenceInfo;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.Log;

public class PostsModel {

	public enum EditPostResult {
		EDITED
	}
	public enum DeletePostResult {
		DELETED, UNKNOWN_ERROR, NOT_YOURS_TO_DELETE, DOESNT_EXIST
	}
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
	
	private boolean checkInputParams(UserEntity user, String subject, String content) {
		return  user==null || user.getUsername()==null || user.getUsername().trim().length()==0 ||
				subject==null || content==null || subject.trim().length()==0 || content.trim().length()==0;
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

	public List<PostEntity> listByModificationDate() {
		TypedQuery<PostEntity> q = mEntityManager.createNamedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE, PostEntity.class);
		List<PostEntity> resultList = q.getResultList();		
		if(resultList==null) return new ArrayList<PostEntity>();
		else return resultList;
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
			Log.info(getClass(), "Error finding by id: " + e.toString());
			return null;
		}
	}

	public DeletePostResult delete(UserEntity userEntity, long postEntityId) {
		if(userEntity==null) {
			Log.info(getClass(), "No user passed to delete method.");
			return DeletePostResult.UNKNOWN_ERROR;
		}
		EntityTransaction trans = mEntityManager.getTransaction();
		try {
			PostEntity pe = findPostById(postEntityId);
			if(pe==null) {
				return DeletePostResult.DOESNT_EXIST;
			}
			else if(!pe.getUser().getUsername().equals(userEntity.getUsername())) {
				return DeletePostResult.NOT_YOURS_TO_DELETE;
			}
			trans.begin();
			mEntityManager.remove(pe);
			trans.commit();
			return DeletePostResult.DELETED;
		} catch(Exception e) {
			Log.info(getClass(), "Error deleting: " + e.toString());
			e.printStackTrace();
			try {
				trans.rollback();
			} catch(Exception e1) {
				Log.info(getClass(), "Error rolling back: " + e.toString());
				e1.printStackTrace();
			}
			return DeletePostResult.UNKNOWN_ERROR;
		} finally {
			EntityUtils.closeEntityConnection(mFactory, mEntityManager);
		}
	}

	public EditPostResult edit(UserEntity userEntity, long num, PostEntity post) {
		return null;
	}

}
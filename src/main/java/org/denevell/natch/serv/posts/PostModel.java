package org.denevell.natch.serv.posts;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.threads.ThreadFactory;
import org.denevell.natch.serv.threads.ThreadModel;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;

public class PostModel {

	public final static String EDITED = "edited";
	public final static String DELETED = "deleted";
	public final static String ADDED = "added";
	public final static String DOESNT_EXIST = "doesntexist";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	public final static String NOT_YOURS_TO_DELETE = "notyourtodelete";
	private EntityManager mEntityManager;
	private PostFactory mPostFactory;
	private ThreadModel mThreadModel;
	
	public PostModel() {
		mPostFactory = new PostFactory();
	}
	
	public void init() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager(); 
		mThreadModel = new ThreadModel(mEntityManager, new ThreadFactory());
	}
	
	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager); 
	}
	
	/**
	 * For testing / di
	 */
	public PostModel(EntityManager entityManager, PostFactory postFactory, ThreadModel threadModel) {
		mPostFactory = postFactory;
		mEntityManager = entityManager;
		mThreadModel = threadModel;
	}
	
	private boolean checkInputParams(UserEntity user, String content) {
		return  user==null || user.getUsername()==null || user.getUsername().trim().length()==0 ||
				content==null || content.trim().length()==0;
	}
	
	public static class Result {
		public String result;
		public long id;
		public Result(String result, long id) {
			this.result = result;
			this.id = id;
		}
	}
	/**
	 * 
	 * @param user
	 * @param content
	 * @param threadId
	 * @return The id of the Result is -1. We shouldn't need the id of the post server side and it /may/ mean more db calls to get it.
	 */
	public Result addPost(UserEntity user, String content, long threadId) {
		EntityTransaction trans = null;
		try {
			PostEntity p = mPostFactory.makePost(content, user);
			if(content==null || checkInputParams(user, p.getContent())) {
				return new Result(BAD_USER_INPUT, -1);
			}
			ThreadEntity thread = mThreadModel.findThreadById(threadId);
			if(thread==null) {
				return new Result(DOESNT_EXIST, -1);
			}
			mPostFactory.addPostToThread(p, thread);
			trans = mEntityManager.getTransaction();
			trans.begin();
			mEntityManager.merge(thread);
			trans.commit();
			return new Result(ADDED, -1);
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			if(trans!=null && trans.isActive()) trans.rollback();
			return new Result(UNKNOWN_ERROR, -1);
		} 
	}

	public List<PostEntity> listByModificationDate(int startPos, int limit) {
		TypedQuery<PostEntity> q = mEntityManager.createNamedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE, PostEntity.class);
		if(startPos<0) startPos=0; if(limit<0) limit=0;
		q.setFirstResult(startPos);
		q.setMaxResults(limit);
		List<PostEntity> resultList = q.getResultList();		
		if(resultList==null) return new ArrayList<PostEntity>();
		if(limit < resultList.size() && resultList.size()>0) resultList.remove(resultList.size()-1); // For some reason we're returning two records on 1 as max results.
		return resultList;
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
			} else if(!pe.getUser().getUsername().equals(userEntity.getUsername())) {
				return NOT_YOURS_TO_DELETE;
			}
			ThreadEntity th = mThreadModel.findThreadById(postEntityId);
			//th = mThreadFactory.updateThreadToRemovePost(th, pe);
			trans.begin();
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

	public String edit(UserEntity userEntity, long postEntityId, String content) {
		EntityTransaction trans = mEntityManager.getTransaction();
		try {
			PostEntity pe = findPostById(postEntityId);
			if(pe==null) {
				return DOESNT_EXIST;
			} else if(!pe.getUser().getUsername().equals(userEntity.getUsername())) {
				return NOT_YOURS_TO_DELETE;
			}
			if(checkInputParams(userEntity, content)) {
				Log.info(this.getClass(), "Edit user: Bad user input");
				return BAD_USER_INPUT;
			}
			// Update post
			ThreadEntity th = mThreadModel.findThreadById(postEntityId);
			if(th==null) {
				return DOESNT_EXIST;
			}
			mPostFactory.setThreadAsUpdatedIfPostIsLatest(th, pe);
			mPostFactory.updatePost(pe, content);
			trans.begin();
			mEntityManager.merge(pe);
			mEntityManager.merge(th);
			trans.commit();
			return EDITED;
		} catch(Exception e) {
			Log.info(getClass(), "Error editing: " + e.toString());
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

	public UserEntity findThreadAuthor(String threadId) {
		try {
			TypedQuery<UserEntity> q = mEntityManager
					.createNamedQuery(ThreadEntity.NAMED_QUERY_FIND_AUTHOR, UserEntity.class);
			q.setParameter(ThreadEntity.NAMED_QUERY_PARAM_ID, threadId);
			List<UserEntity> resultList = q.getResultList();		
			if(resultList==null || resultList.size()==0) return null;
			else return resultList.get(0);
		} catch(Exception e) {
			Log.info(getClass(), "Error finding author by thread by id: " + e.toString());
			return null;
		} 		
	}

}
package org.denevell.natch.serv.post.edit;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;

public class EditPostModel {

	public final static String EDITED = "edited";
	public final static String DELETED = "deleted";
	public final static String ADDED = "added";
	public final static String DOESNT_EXIST = "doesntexist";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	public final static String NOT_YOURS_TO_DELETE = "notyourtodelete";	
	private EntityManager mEntityManager;
	
	public EditPostModel() {
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
	public EditPostModel(EntityManagerFactory factory, EntityManager entityManager) {
		mEntityManager = entityManager;
	}
	
	public String edit(UserEntity userEntity, 
			long postEntityId, 
			PostEntity mPe,
			boolean isEditingThread) {
		EntityTransaction trans = mEntityManager.getTransaction();
		try {
			if(userEntity==null || mPe==null) {
				Log.info(getClass(), "No user or postadapter passed to edit method");
				return UNKNOWN_ERROR;
			}
			PostEntity pe = findPostById(postEntityId);
			if(pe==null) {
				return DOESNT_EXIST;
			} else if(!userEntity.isAdmin() && !pe.getUser().getUsername().equals(userEntity.getUsername())) {
				return NOT_YOURS_TO_DELETE;
			}
			mPe.setCreated(pe.getCreated());
			mPe.setId(pe.getId());
			mPe.setThreadId(pe.getThreadId());
			mPe.setModified(new Date().getTime());
			if(!userEntity.getUsername().equals(pe.getUser().getUsername()) && userEntity.isAdmin()) {
			   mPe.setUser(pe.getUser()); 
			} else {
			    mPe.setUser(userEntity);			
			}
			if(!isEditingThread)  mPe.setSubject("-");
			if(isBadInputParams(userEntity, mPe.getSubject(), mPe.getContent(), isEditingThread)) {
				Log.info(this.getClass(), "Edit user: Bad user input");
				return BAD_USER_INPUT;
			}
			trans.begin();
			mEntityManager.merge(mPe);
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
	
	
	public static boolean isBadInputParams(UserEntity user, String subject, String content, boolean isEditingThread) {
		return  user==null || 
				user.getUsername()==null || 
				user.getUsername().trim().length()==0 ||
				(isEditingThread && subject==null) || 
				(isEditingThread && subject.trim().length()==0) || 
				content==null || 
				content.trim().length()==0;
	}	

}
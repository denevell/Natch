package org.denevell.natch.serv.post.edit;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.CallDbBuilder;
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
	private CallDbBuilder<PostEntity> mPostModel;
	
	public EditPostModel() {
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
	public EditPostModel(EntityManagerFactory factory, EntityManager entityManager) {
		mEntityManager = entityManager;
	}
	
	public String edit(UserEntity userEntity, 
			long postEntityId, 
			PostEntity postToEdit,
			boolean isEditingThread) {
		EntityTransaction trans = mEntityManager.getTransaction();
		try {
			PostEntity pe = mPostModel.find(postEntityId, false, mEntityManager, PostEntity.class);
			if(pe==null) {
				return DOESNT_EXIST;
			} else if(!userEntity.isAdmin() && !pe.getUser().getUsername().equals(userEntity.getUsername())) {
				return NOT_YOURS_TO_DELETE;
			}
			postToEdit.setCreated(pe.getCreated());
			postToEdit.setId(pe.getId());
			postToEdit.setThreadId(pe.getThreadId());
			postToEdit.setModified(new Date().getTime());
			if(!userEntity.getUsername().equals(pe.getUser().getUsername()) && userEntity.isAdmin()) {
			   postToEdit.adminEdited();
			   postToEdit.setUser(pe.getUser()); 
			} else {
			    postToEdit.setUser(userEntity);			
			}
			if(!isEditingThread) postToEdit.setSubject("-");
			if(isBadInputParams(userEntity, postToEdit.getSubject(), postToEdit.getContent(), isEditingThread)) {
				Log.info(this.getClass(), "Edit user: Bad user input");
				return BAD_USER_INPUT;
			}
			trans.begin();
			mEntityManager.merge(postToEdit);
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
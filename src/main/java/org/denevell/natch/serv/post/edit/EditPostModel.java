package org.denevell.natch.serv.post.edit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.Log;

public class EditPostModel {

	public final static String EDITED = "edited";
	public final static String DELETED = "deleted";
	public final static String ADDED = "added";
	public final static String DOESNT_EXIST = "doesntexist";
	public final static String UNKNOWN_ERROR = "unknownerror";
	public final static String BAD_USER_INPUT = "baduserinput";
	public final static String NOT_YOURS_TO_DELETE = "notyourtodelete";	
	private CallDbBuilder<PostEntity> mPostModel;
	
	public EditPostModel() {
		mPostModel = new CallDbBuilder<PostEntity>();
	}
	
	/**
	 * For testing / di
	 */
	public EditPostModel(EntityManagerFactory factory, EntityManager entityManager) {
	}
	
	/**
	 * 1. Get the current post for this id
	 * 2. Return if doesn't exist
	 * 3. Return if it's not yours to edit
	 * 4. Set the entity with edited subject and content with fields from database entity
	 * 5. Return if the newly merged post is badly formatted 
	 * 6. Merge post
	 * @return
	 */
	public String edit(UserEntity userEntity, 
			long postEntityId, 
			PostEntity postToEdit,
			boolean isEditingThread) {
		PostEntity pe = mPostModel
				.startTransaction()
				.find(postEntityId, false, PostEntity.class);
		if(pe==null) {
			mPostModel.commitAndCloseEntityManager();
			return DOESNT_EXIST;
		} else if(!userEntity.isAdmin() && !pe.getUser().getUsername().equals(userEntity.getUsername())) {
			mPostModel.commitAndCloseEntityManager();
			return NOT_YOURS_TO_DELETE;
		}
		postToEdit.setFieldsFromOtherEntity(pe, isEditingThread, userEntity);
		if(isBadInputParams(userEntity, postToEdit.getSubject(), postToEdit.getContent(), isEditingThread)) {
			Log.info(this.getClass(), "Edit user: Bad user input");
			mPostModel.commitAndCloseEntityManager();
			return BAD_USER_INPUT;
		}
		mPostModel.getEntityManager().merge(postToEdit);
		mPostModel.commitAndCloseEntityManager();
		return EDITED;
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
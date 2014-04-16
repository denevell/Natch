package org.denevell.natch.model.impl;

import java.util.Date;

import javax.inject.Singleton;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.UserEntity;
import org.denevell.natch.model.interfaces.PostEditModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PostEditModelImpl implements PostEditModel { 

	private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory); 
	public int edit(
			final long id, 
			final UserEntity userEntity,
			final PostEntity editedPostEntity) {
		int result = mPostModel.
				startTransaction().
				updateEntityOnPermission(id,
				new Jrappy.UpdateItemOnPermissionCorrect<PostEntity>() {
					@Override
					public boolean update(PostEntity item) {
						if(!userEntity.isAdmin() && !item.getUser().getUsername().equals(userEntity.getUsername())) {
							return false;
						}
						item.setContent(editedPostEntity.getContent());
						if(editedPostEntity.getSubject()!=null) item.setSubject(editedPostEntity.getSubject());
						if(editedPostEntity.getTags()!=null) item.setTags(editedPostEntity.getTags());
                        item.setModified(new Date().getTime());
                        if(!userEntity.getUsername().equals(item.getUser().getUsername()) && userEntity.isAdmin()) {
                        	item.adminEdited();
                        }
						return true;
					}
				},
				PostEntity.class);
		mPostModel.commitAndCloseEntityManager();
		switch (result) {
		case Jrappy.NOT_FOUND:
			return PostEditModel.DOESNT_EXIST;
		case Jrappy.UPDATED:
			return PostEditModel.EDITED;
		case Jrappy.PERMISSION_DENIED:
			return PostEditModel.NOT_YOURS;
		default:
			return -1;
		}
	}

}
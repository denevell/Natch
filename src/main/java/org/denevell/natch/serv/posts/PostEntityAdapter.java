package org.denevell.natch.serv.posts;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.resources.EditPostResource;

public interface PostEntityAdapter {
	public void setPostWithNewData(EditPostResource resource);
	/** 
	 * Used to take the object already in the class, via setPostWithNewData,
	 * and use the parameter to set the parts which shouldn't change.
	 * 
	 * Eg, setPostWithNewData is used in a REST class to set new data that has
	 * been specified by the user, and then the model uses this method to ensure
	 * we're setting the correct id and creation date.
	 * @param pe
	 * @param userEntity
	 * @return
	 */
	public PostEntity createPost(PostEntity pe, UserEntity userEntity);
	
}

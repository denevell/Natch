package org.denevell.natch.serv.posts;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;

public interface PostEntityAdapter {
	/** 
	 * So the rest class can say "Okay I want to update resource, 
	 * and I want it to be like this. And the model can be like "Okay, 
	 * sure, but I'm going to modify it before proceeding in this method
	 * here".
	 */
	public PostEntity createPost(PostEntity pe, UserEntity userEntity);
}

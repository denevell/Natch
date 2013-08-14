package org.denevell.natch.serv.posts;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;

public class AddPostResourcePostEntityAdapter implements PostEntityAdapter {
	
	private PostEntity mPost;

	public void create(AddPostResourceInput input) {
		mPost = new PostEntity();
		mPost.setContent(input.getContent());
	}

	@Override
	public PostEntity createPost(PostEntity pe, UserEntity userEntity) {
		mPost.setUser(userEntity);
		long created = new Date().getTime();
		mPost.setCreated(created);
		mPost.setModified(created);
		return mPost;
	}
	
	/**
	 * Not necessarily saved to database if error.
	 * Use the return of whatever uses this to see if the db persist was successful
	 * @return
	 */
	public PostEntity getCreatedPost() {
		return mPost;
	}
	
}

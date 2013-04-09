package org.denevell.natch.serv.posts;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.posts.resources.EditPostResource;

public class EditPostResourcePostEntityAdapter implements PostEntityAdapter {
	
	private PostEntity mPe;
	
	public void setPostWithNewData(EditPostResource resource) {
		mPe = new PostEntity();
		mPe.setContent(resource.getContent());
		mPe.setSubject(resource.getSubject());
	}

	public PostEntity createPost(PostEntity pe, UserEntity userEntity) {
		mPe.setCreated(pe.getCreated());
		mPe.setId(pe.getId());
		mPe.setModified(new Date().getTime());
		mPe.setUser(userEntity);
		return mPe;
	}

}

package org.denevell.natch.serv.posts;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.EditPostResource;

public class EditPostResourcePostEntityAdapter implements PostEntityAdapter {
	
	private PostEntity mPe;
	
	public void setPostWithNewData(EditPostResource resource) {
		mPe = new PostEntity();
		mPe.setContent(resource.getContent());
		mPe.setTags(resource.getTags());
	}

	public PostEntity createPost(PostEntity pe, UserEntity userEntity) {
		mPe.setCreated(pe.getCreated());
		mPe.setId(pe.getId());
		mPe.setThreadId(pe.getThreadId());
		mPe.setModified(new Date().getTime());
		mPe.setUser(userEntity);
		return mPe;
	}

}

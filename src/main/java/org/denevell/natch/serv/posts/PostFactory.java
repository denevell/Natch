package org.denevell.natch.serv.posts;

import java.util.ArrayList;
import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.Log;

public class PostFactory {

	public PostEntity makePost(String content, UserEntity user) {
		long time = new Date().getTime();
		return new PostEntity(user, time, time, content);
	}

	public void addPostToThread(PostEntity p, ThreadEntity thread) {
		if(thread.getPosts()==null) {
			thread.setPosts(new ArrayList<PostEntity>());
		}
		thread.getPosts().add(p);
		setThreadAsUpdated(thread);
	}

	public void setThreadAsUpdatedIfPostIsLatest(ThreadEntity thread, PostEntity pe) {
		if(thread.getThreadModified()==pe.getModified()) {
			setThreadAsUpdated(thread);
		} else {
			Log.info(getClass(), "Didn't update thread's threadModified since edited thread doesn't seem to be the latest.");
		}
	}

	public void setThreadAsUpdated(ThreadEntity thread) {
		long time = new Date().getTime();
		thread.setThreadModified(time);
	}

	public void updatePost(PostEntity pe, String content) {
		pe.setContent(content);
		long time = new Date().getTime();
		pe.setModified(time);
	}
}

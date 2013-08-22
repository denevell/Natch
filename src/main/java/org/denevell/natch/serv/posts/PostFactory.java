package org.denevell.natch.serv.posts;

import java.util.ArrayList;
import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;

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
		long time = new Date().getTime();
		thread.setThreadModified(time);
	}
}

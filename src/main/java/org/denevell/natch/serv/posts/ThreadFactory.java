package org.denevell.natch.serv.posts;

import java.util.Arrays;
import java.util.List;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;

public class ThreadFactory {

	public ThreadEntity makeThread() {
		return null;
	}

	public ThreadEntity makeThread(PostEntity p) {
		ThreadEntity threadEntity = new ThreadEntity(p, Arrays.asList(p));
		threadEntity.setId(p.getThreadId());
		return threadEntity;
	}

	public ThreadEntity makeThread(ThreadEntity thread, PostEntity p) {
		thread.setLatestPost(p);
		List<PostEntity> posts = thread.getPosts();
		if(posts!=null) {
			posts.add(p);
		} else {
			posts = Arrays.asList(p);
		}
		thread.setPosts(posts);
		return thread;
	}
}

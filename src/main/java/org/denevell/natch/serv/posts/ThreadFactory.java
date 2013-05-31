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

	public ThreadEntity updateThreadToRemovePost(ThreadEntity th, PostEntity pe) {
		th.getPosts().remove(pe);
		if(th.getRootPost()!=null && th.getRootPost().getId()==pe.getId()) {
			th.setRootPost(null);
		}
		if(th.getLatestPost()!=null && th.getLatestPost().getId()==pe.getId() && th.getPosts()!=null && th.getPosts().size()>=1) {
			th.setLatestPost(th.getPosts().get(th.getPosts().size()-1));
		}
		return th;
	}
}

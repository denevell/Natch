package org.denevell.natch.serv.threads;

import java.util.List;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;

public class ThreadFactory {

	public ThreadEntity updateThreadToRemovePost(ThreadEntity th, PostEntity pe) {
		th.getPosts().remove(pe);
		if(th.getLatestPost()!=null && th.getLatestPost().getId()==pe.getId() && th.getPosts()!=null && th.getPosts().size()>=1) {
			th.setLatestPost(th.getPosts().get(th.getPosts().size()-1));
		}
		return th;
	}

	public ThreadEntity makeThread(String subject, String content,
			List<String> tags, UserEntity user) {
		return new ThreadEntity(subject, content, tags, user);
	}
}

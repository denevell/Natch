package org.denevell.natch.serv.threads;

import java.util.List;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;

public class ThreadFactory {

	public ThreadEntity makeThread(String subject, String content,
			List<String> tags, UserEntity user) {
		return new ThreadEntity(subject, content, tags, user);
	}
}

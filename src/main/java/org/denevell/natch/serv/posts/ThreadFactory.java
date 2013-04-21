package org.denevell.natch.serv.posts;

import java.util.Arrays;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;

public class ThreadFactory {

	public ThreadEntity makeThread() {
		return null;
	}

	public ThreadEntity makeThread(PostEntity p) {
		return new ThreadEntity(p, Arrays.asList(p));
	}
}

package org.denevell.natch.serv.posts;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;

public class PostFactory {

	public PostEntity makePost(String content, UserEntity user) {
		long time = new Date().getTime();
		return new PostEntity(user, time, time, content);
	}
}

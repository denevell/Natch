package org.denevell.natch.serv.posts;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;

public class PostFactory {

	public PostEntity createPost(UserEntity user, String subject, String content) {
		long time = new Date().getTime();
		return new PostEntity(user, time, time, subject, content, null); 
	}	
}

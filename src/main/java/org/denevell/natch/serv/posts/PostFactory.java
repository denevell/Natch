package org.denevell.natch.serv.posts;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;

public class PostFactory {

	public PostEntity createPost(UserEntity user, String subject, String content, String threadId) {
		long time = new Date().getTime();
		if(threadId==null) {
			threadId = String.valueOf(time)+String.valueOf(subject.hashCode());
		}
		return new PostEntity(user, time, time, subject, content, threadId); 
	}	
}

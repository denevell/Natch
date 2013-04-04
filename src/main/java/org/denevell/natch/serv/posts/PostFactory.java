package org.denevell.natch.serv.posts;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;

public class PostFactory {

	public PostEntity createPost(String subject, String content) {
		long time = new Date().getTime();
		return new PostEntity(time, time, subject, content, null); 
	}	
}

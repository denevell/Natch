package org.denevell.natch.serv.posts;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.Log;

public class PostFactory {

	public PostEntity createPost(UserEntity user, String subject, String content, String threadId) {
		long time = new Date().getTime();
		threadId = getThreadId(subject, threadId, time);
		return new PostEntity(user, time, time, subject, content, threadId); 
	}

	private String getThreadId(String subject, String threadId, long time) {
		if(threadId==null) {
			try {
				MessageDigest md5Algor = MessageDigest.getInstance("MD5");
				StringBuffer sb = new StringBuffer();
				byte[] digest = md5Algor.digest(subject.getBytes());
				for (byte b : digest) {
					sb.append(Integer.toHexString((int) (b & 0xff)));
				}				
				threadId = sb.toString();
			} catch (NoSuchAlgorithmException e) {
				Log.info(getClass(), "Couldn't get an MD5 hash. I guess we'll just use hashCode() then.");
				e.printStackTrace();
				threadId = String.valueOf(subject.hashCode());
			}
			threadId = threadId+String.valueOf(time);
		}
		return threadId;
	}	
}

package org.denevell.natch.serv.posts;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.utils.Log;

public class AddPostResourcePostEntityAdapter implements PostEntityAdapter {
	
	private PostEntity mPost;

	public void create(AddPostResourceInput input) {
		mPost = new PostEntity();
		mPost.setContent(input.getContent());
		mPost.setSubject(input.getSubject());
		long created = new Date().getTime();
		String thread = getThreadId(input.getSubject(), input.getThreadId(), created);
		mPost.setThreadId(thread);
		mPost.setTags(input.getTags());
	}

	@Override
	public PostEntity createPost(PostEntity pe, UserEntity userEntity) {
		mPost.setUser(userEntity);
		long created = new Date().getTime();
		mPost.setCreated(created);
		mPost.setModified(created);
		return mPost;
	}
	
	private String getThreadId(String subject, String threadId, long time) {
		if(threadId==null || threadId.trim().length()==0) {
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

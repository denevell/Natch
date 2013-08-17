package org.denevell.natch.serv.posts;

import java.util.ArrayList;
import java.util.List;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.io.threads.ThreadResource;

public class ThreadResourceAdapter extends ThreadResource {

	public ThreadResourceAdapter(String threadAuthor, String subject, List<PostEntity> posts) {
		super(null, null, 0l, 0l, 0l, 0l, null);
		List<PostResource> postsResources = new ArrayList<PostResource>();
		for (PostEntity p: posts) {
			PostResource postResource = new PostResource(p.getUser().getUsername(), 
					p.getCreated(), 
					p.getModified(), 
					p.getContent(),
					p.getTags());
			postResource.setId(p.getId());
			postResource.setThreadId(p.getThreadId());
			postsResources.add(postResource);
		}
		if(postsResources.size()>0) {
			setSubject(subject);
			setAuthor(threadAuthor);
		}
		setPosts(postsResources);
	}

}

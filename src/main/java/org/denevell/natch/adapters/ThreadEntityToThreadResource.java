package org.denevell.natch.adapters;

import java.util.ArrayList;
import java.util.List;

import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;

public class ThreadEntityToThreadResource {
	
	public static ThreadResource adapt(ThreadEntity thread) {
		ThreadResource tr = new ThreadResource();
		List<PostResource> postsResources = new ArrayList<PostResource>();
		for (PostEntity p: thread.getPosts()) {
			PostResource postResource = new PostResource(p.getUser().getUsername(), 
					p.getCreated(), 
					p.getModified(), 
					p.getSubject(), 
					p.getContent(),
					p.getTags(), 
					p.isAdminEdited());
			postResource.setId(p.getId());
			postResource.setThreadId(p.getThreadId());
			postsResources.add(postResource);
		}
		if(postsResources.size()>0) {
			tr.setSubject(postsResources.get(0).getSubject());
			tr.setAuthor(thread.getRootPost().getUser().getUsername());
			tr.setTags(postsResources.get(0).getTags());
			tr.setModification(postsResources.get(0).getModification());
		}
		tr.setPosts(postsResources);
		tr.setNumPosts((int) thread.getNumPosts());
		tr.setId(thread.getId());
		return tr;
	}	
		

}

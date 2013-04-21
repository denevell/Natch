package org.denevell.natch.serv.posts;

import java.util.ArrayList;
import java.util.List;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.serv.posts.resources.ListPostsResource;
import org.denevell.natch.serv.posts.resources.PostResource;

public class ListThreadsResourceAdapter extends ListPostsResource {

	public ListThreadsResourceAdapter(List<ThreadEntity> threads) {
		List<PostResource> postsResources = new ArrayList<PostResource>();
		for (ThreadEntity p: threads) {
			PostResource postResource = new PostResource(
					p.getRootPost().getUser().getUsername(), 
					p.getRootPost().getCreated(), 
					p.getRootPost().getModified(), 
					p.getRootPost().getSubject(), 
					p.getRootPost().getContent(),
					p.getRootPost().getTags());
			postResource.setId(p.getRootPost().getId());
			postResource.setThreadId(p.getRootPost().getThreadId());
			postsResources.add(postResource);
		}
		setPosts(postsResources);
	}

}

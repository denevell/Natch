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
			PostResource postResource = new PostResource(p.getLatestPost().getUser().getUsername(), 
					p.getLatestPost().getCreated(), 
					p.getLatestPost().getModified(), 
					p.getLatestPost().getSubject(), 
					p.getLatestPost().getContent(),
					p.getLatestPost().getTags());
			postResource.setId(p.getLatestPost().getId());
			postResource.setThreadId(p.getLatestPost().getThreadId());
			postsResources.add(postResource);
		}
		setPosts(postsResources);
	}

}

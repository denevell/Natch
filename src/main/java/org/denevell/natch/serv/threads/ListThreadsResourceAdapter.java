package org.denevell.natch.serv.threads;

import java.util.ArrayList;
import java.util.List;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;

public class ListThreadsResourceAdapter extends ListPostsResource {

	public ListThreadsResourceAdapter(List<ThreadEntity> threads) {
		List<PostResource> postsResources = new ArrayList<PostResource>();
		for (ThreadEntity p: threads) {
			PostResource postResource = new PostResource(
					p.getUser().getUsername(), 
					p.getCreated(), 
					p.getLatestPost().getModified(), 
					p.getContent(),
					p.getTags());
			postResource.setId(p.getId());
			postResource.setThreadId(p.getId());
			postsResources.add(postResource);
		}
		setPosts(postsResources);
	}

}

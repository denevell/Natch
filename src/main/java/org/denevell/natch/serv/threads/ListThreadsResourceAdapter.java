package org.denevell.natch.serv.threads;

import java.util.ArrayList;
import java.util.List;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;
import org.denevell.natch.utils.Log;

public class ListThreadsResourceAdapter extends ListPostsResource {

	public ListThreadsResourceAdapter(List<ThreadEntity> threads) {
		List<PostResource> postsResources = new ArrayList<PostResource>();
		for (ThreadEntity p: threads) {
			if(p.getRootPost()==null) {
				reportNullRootThreadError(p);
				continue;
			} else {
				PostResource postResource = new PostResource(
						p.getRootPost().getUser().getUsername(), 
						p.getRootPost().getCreated(), 
						p.getLatestPost().getModified(), 
						p.getRootPost().getSubject(), 
						p.getRootPost().getContent(),
						p.getRootPost().getTags());
				postResource.setId(p.getRootPost().getId());
				postResource.setThreadId(p.getRootPost().getThreadId());
				postsResources.add(postResource);
			}
		}
		setPosts(postsResources);
	}

	private void reportNullRootThreadError(ThreadEntity p) {
		if(p.getId()!=null) {
			Log.info(getClass(), "Found a thread with a null root post. Thread: " + p.getId());
		} else {
			Log.info(getClass(), "Found a thread with a null root post. Unknown thread id.");
		}
	}

}

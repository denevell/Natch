package org.denevell.natch.serv.list_threads;

import java.util.ArrayList;
import java.util.List;

import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.io.threads.ListThreadsResource;
import org.denevell.natch.io.threads.ThreadResource;
import org.denevell.natch.utils.Log;

public class ListThreadsResourceAdapter extends ListThreadsResource {

	public ListThreadsResourceAdapter(List<ThreadEntity> threads) {
		List<ThreadResource> postsResources = new ArrayList<ThreadResource>();
		for (ThreadEntity p: threads) {
			if(p.getRootPost()==null) {
				reportNullRootThreadError(p);
				continue;
			} else {
				ThreadResource postResource = new ThreadResource();
				postResource.setAuthor(p.getRootPost().getUser().getUsername());
				postResource.setNumPosts((int) p.getNumPosts());
				postResource.setSubject(p.getRootPost().getSubject());
				postResource.setTags(p.getRootPost().getTags());
				postResource.setModification(p.getRootPost().getModified());
				postResource.setCreation(p.getRootPost().getCreated());
				postResource.setId(p.getId());
				postsResources.add(postResource);
			}
		}
		setThreads(postsResources);
	}

	private void reportNullRootThreadError(ThreadEntity p) {
		if(p.getId()!=null) {
			Log.info(getClass(), "Found a thread with a null root post. Thread: " + p.getId());
		} else {
			Log.info(getClass(), "Found a thread with a null root post. Unknown thread id.");
		}
	}

}

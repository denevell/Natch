package org.denevell.natch.serv.posts;

import java.util.ArrayList;
import java.util.List;

import org.denevell.natch.db.entities.PostEntity;

public class ListPostsResourceAdapter extends ListPostsResource {

	public ListPostsResourceAdapter(List<PostEntity> posts) {
		List<PostResource> postsResources = new ArrayList<PostResource>();
		for (PostEntity p: posts) {
			PostResource postResource = new PostResource(p.getUser().getUsername(), 
					p.getCreated(), 
					p.getModified(), 
					p.getSubject(), 
					p.getContent());
			postResource.setId(p.getId());
			postsResources.add(postResource);
		}
		setPosts(postsResources);
	}

}

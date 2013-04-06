package org.denevell.natch.serv.posts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListPostsResource {
	
	private List<PostResource> posts = new ArrayList<PostResource>();

	public List<PostResource> getPosts() {
		return posts;
	}

	public void setPosts(List<PostResource> posts) {
		this.posts = posts;
	}

}

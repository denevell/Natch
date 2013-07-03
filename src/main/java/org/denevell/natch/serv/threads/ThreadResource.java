package org.denevell.natch.serv.threads;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.denevell.natch.io.posts.PostResource;

@XmlRootElement
public class ThreadResource {

	private String subject;
	private List<PostResource> posts = new ArrayList<PostResource>();

	public String getSubject() {
		return subject;
	}

	public void setSubject(String title) {
		this.subject = title;
	}

	public List<PostResource> getPosts() {
		return posts;
	}

	public void setPosts(List<PostResource> posts) {
		this.posts = posts;
	}
}

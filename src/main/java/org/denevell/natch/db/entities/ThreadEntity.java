package org.denevell.natch.db.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
@Entity
public class ThreadEntity {
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@OneToOne
	private PostEntity latestPost;
	@OneToMany(cascade=CascadeType.PERSIST) 
	private List<PostEntity> posts;
	
	public ThreadEntity() {
	}
	
	public ThreadEntity(PostEntity latestPost, List<PostEntity> posts) {
		this.latestPost = latestPost;
		this.posts = posts;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PostEntity getLatestPost() {
		return latestPost;
	}

	public void setLatestPost(PostEntity latestPost) {
		this.latestPost = latestPost;
	}

	public List<PostEntity> getPosts() {
		return posts;
	}

	public void setTags(List<PostEntity> posts) {
		this.posts = posts;
	}
}

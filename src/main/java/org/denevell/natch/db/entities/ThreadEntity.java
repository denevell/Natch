package org.denevell.natch.db.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@NamedQueries(
	{
	@NamedQuery(name=ThreadEntity.NAMED_QUERY_LIST_THREADS,query=
		"select p from ThreadEntity p order by p.latestPost.created asc")
	,
	@NamedQuery(name=ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID,query=
		"select p from ThreadEntity p where p.id = :" + ThreadEntity.NAMED_QUERY_PARAM_ID)
	}
)
@Entity
public class ThreadEntity {
	@Id 
	private String id;
	@OneToOne
	private PostEntity latestPost;
	@OneToMany(cascade=CascadeType.PERSIST) 
	private List<PostEntity> posts;
	public static final String NAMED_QUERY_LIST_THREADS = "findThreads";
	public static final String NAMED_QUERY_FIND_THREAD_BY_ID = "findThreadById";
	public static final String NAMED_QUERY_PARAM_ID = "id";
	
	public ThreadEntity() {
	}
	
	public ThreadEntity(PostEntity latestPost, List<PostEntity> posts) {
		this.latestPost = latestPost;
		this.posts = posts;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public void setPosts(List<PostEntity> posts) {
		this.posts = posts;
	}
}
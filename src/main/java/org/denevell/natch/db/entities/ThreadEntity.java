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
		"select p from ThreadEntity p order by p.latestPost.created desc")
	,
	@NamedQuery(name=ThreadEntity.NAMED_QUERY_LIST_THREADS_BY_TAG,query=
		"select p from ThreadEntity p where :" + ThreadEntity.NAMED_QUERY_PARAM_TAG +  " member of p.rootPost.tags order by p.latestPost.created desc")
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
	@OneToOne
	private PostEntity rootPost;
	public static final String NAMED_QUERY_LIST_THREADS = "findThreads";
	public static final String NAMED_QUERY_LIST_THREADS_BY_TAG = "findThreadByTag";
	public static final String NAMED_QUERY_FIND_THREAD_BY_ID = "findThreadById";
	public static final String NAMED_QUERY_PARAM_ID = "id";
	public static final String NAMED_QUERY_PARAM_TAG = "tag";
	
	public ThreadEntity() {
	}
	
	public ThreadEntity(PostEntity initialPost, List<PostEntity> posts) {
		this.latestPost = initialPost;
		this.setRootPost(initialPost);
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

	public PostEntity getRootPost() {
		return rootPost;
	}

	public void setRootPost(PostEntity rootPost) {
		this.rootPost = rootPost;
	}
}

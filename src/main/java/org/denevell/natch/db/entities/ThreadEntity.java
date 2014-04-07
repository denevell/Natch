package org.denevell.natch.db.entities;

import java.util.List;

public class ThreadEntity {
	private String id;
	private long numPosts;
	private PostEntity latestPost;
	private List<PostEntity> posts;
	private PostEntity rootPost;
	public static final String NAMED_QUERY_LIST_THREADS = "findThreads";
	public static final String NAMED_QUERY_LIST_THREADS_BY_TAG = "findThreadByTag";
	public static final String NAMED_QUERY_FIND_THREAD_BY_ID = "findThreadById";
	public static final String NAMED_QUERY_FIND_AUTHOR = "findAuthorById";
	public static final String NAMED_QUERY_COUNT_THREADS= "countThreads";
	public static final String NAMED_QUERY_COUNT_THREAD_BY_TAG = "countThreadsWithTag";
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

	public long getNumPosts() {
		return numPosts;
	}

	public void setNumPosts(long numPosts) {
		this.numPosts = numPosts;
	}

	public void updateThreadToRemovePost(PostEntity pe) {
		getPosts().remove(pe);
		if(getRootPost()!=null && getRootPost().getId()==pe.getId()) {
			setRootPost(null);
		}
		if(getLatestPost()!=null && getLatestPost().getId()==pe.getId() && getPosts()!=null && getPosts().size()>=1) {
			setLatestPost(getPosts().get(getPosts().size()-1));
		}
		setNumPosts(getNumPosts()-1);
	}	

}

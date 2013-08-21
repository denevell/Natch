package org.denevell.natch.db.entities;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

public class ThreadEntity {
	private long id;
	private PostEntity latestPost;
	private List<PostEntity> posts;
	private List<String> tags;
	private String subject;
	private String content;
	private UserEntity user;
	private long created;
	private long modified;	
	private long threadModified;
	public static final String NAMED_QUERY_LIST_THREADS = "findThreads";
	public static final String NAMED_QUERY_LIST_THREADS_BY_TAG = "findThreadByTag";
	public static final String NAMED_QUERY_FIND_THREAD_BY_ID = "findThreadById";
	public static final String NAMED_QUERY_FIND_AUTHOR = "findAuthorById";
	public static final String NAMED_QUERY_PARAM_ID = "id";
	public static final String NAMED_QUERY_PARAM_TAG = "tag";
	public static final String NAMED_QUERY_FIND_NUMBER_OF_THREADS = "findNumberOfThreads";
	public static final String NAMED_QUERY_FIND_NUMBER_OF_THREADS_BY_TAG = "findNumberOfThreadsByTag";
	
	public ThreadEntity() {
	}
	
	public ThreadEntity(String subject, String content, List<String> tags, UserEntity user) {
		this.subject = subject;
		this.content = content;
		this.tags = tags;
		this.user = user;
		long time = new Date().getTime();
		this.created = time;
		this.modified = time;
		this.threadModified = time;
	}
	
	public String getSubject() {
		String escaped = StringEscapeUtils.escapeHtml4(subject);
		return escaped;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public void setPosts(List<PostEntity> posts) {
		this.posts = posts;
	}

	public List<String> getTags() {
		if(tags==null) return null;
		for (int i = 0; i < tags.size(); i++) {
			String string = StringEscapeUtils.escapeHtml4(tags.get(i));
			tags.set(i, string);
		}
		return tags;		
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public long getThreadModified() {
		return threadModified;
	}

	public void setThreadModified(long threadModified) {
		this.threadModified = threadModified;
	}

}

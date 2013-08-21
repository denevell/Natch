package org.denevell.natch.db.entities;

import org.apache.commons.lang3.StringEscapeUtils;

public class PostEntity {
	
	public static final String NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE = "findByModData";
	public static final String NAMED_QUERY_PARAM_ID= "id";
	public static final String NAMED_QUERY_FIND_BY_ID = "findById";
	
	private long id;
	private long created;
	private long modified;
	private String content;
	private UserEntity user;
	
	public PostEntity() {
	}
	
	public PostEntity(UserEntity user, long created, long modified, String content) {
		this.user = user;
		this.created = created;
		this.modified = modified;
		this.content = content;
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

	public String getContent() {
		String escaped = StringEscapeUtils.escapeHtml4(content);
		return escaped;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

}

package org.denevell.natch.db.entities;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

public class PostEntity {
	
	public static final String NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE = "findByModData";
	public static final String NAMED_QUERY_FIND_BY_THREADID = "findByThreadId";
	public static final String NAMED_QUERY_PARAM_ID= "id";
	public static final String NAMED_QUERY_PARAM_THREADID = "threadId";
	public static final String NAMED_QUERY_FIND_BY_ID = "findById";
    public static final int MAX_TAG_LENGTH = 20;
    public static final int MAX_SUBJECT_LENGTH = 300;
	
	private long id;
	private long created;
	private long modified;
	private String subject;
	private String content;
	private String threadId;
	private List<String> tags;
	private UserEntity user;
    private boolean adminEdited = false;
	
	public PostEntity() {
	}
	
	public PostEntity(UserEntity user, long created, long modified, String subject, String content, String threadId) {
		this.user = user;
		this.created = created;
		this.modified = modified;
		this.subject = subject;
		this.content = content;
		this.threadId = threadId;
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

	public String getSubject() {
		String escaped = StringEscapeUtils.escapeHtml4(subject);
		return escaped;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		String escaped = StringEscapeUtils.escapeHtml4(content);
		return escaped;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
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

    public void adminEdited() {
        adminEdited = true;
    }
    
    public boolean isAdminEdited() {
        return adminEdited;
    }
}

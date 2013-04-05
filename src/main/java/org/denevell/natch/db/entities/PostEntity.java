package org.denevell.natch.db.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PostEntity {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private long created;
	private long modified;
	private String subject;
	private String content;
	private String threadId;
	@OneToOne
	private UserEntity user;
	
	public PostEntity(UserEntity user, long created, long modified, String subject, String content, String threadId) {
		this.setUser(user);
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
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
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
}

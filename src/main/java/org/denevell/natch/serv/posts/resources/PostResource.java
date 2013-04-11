package org.denevell.natch.serv.posts.resources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PostResource {

	private long id;
	private String username;
	private String subject;
	private String content;
	private String threadId;
	private long creation;
	private long modification;
	
	public PostResource() {
	}
	
	public PostResource(String username, long created, long modified, String subject, String content) {
		this.username = username;
		this.creation = created;
		this.modification = modified;
		this.subject = subject;
		this.content = content;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
	public long getCreation() {
		return creation;
	}
	public void setCreation(long creation) {
		this.creation = creation;
	}
	public long getModification() {
		return modification;
	}
	public void setModification(long modification) {
		this.modification = modification;
	}
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
}

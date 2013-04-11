package org.denevell.natch.serv.posts.resources;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddPostResourceInput {
	
	private String subject;
	private String content;
	private String thread;
	
	public AddPostResourceInput() {
	}
	
	public AddPostResourceInput(String subject, String content) {
		this.subject = subject;
		this.content = content;
	}
	
	public AddPostResourceInput(String subject, String content, String threadId) {
		this.subject = subject;
		this.content = content;
		this.thread = threadId;
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

	public String getThread() {
		return thread;
	}

	@XmlElement(required=false)
	public void setThread(String thread) {
		this.thread = thread;
	}

}

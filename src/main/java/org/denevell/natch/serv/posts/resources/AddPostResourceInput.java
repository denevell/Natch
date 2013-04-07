package org.denevell.natch.serv.posts.resources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddPostResourceInput {
	
	private String subject;
	private String content;
	
	public AddPostResourceInput() {
	}
	
	public AddPostResourceInput(String subject, String content) {
		this.subject = subject;
		this.content = content;
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

}

package org.denevell.natch.serv.posts.resources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EditPostResource {

	private String content;
	private String subject;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

}

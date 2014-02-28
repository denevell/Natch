package org.denevell.natch.serv.push;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PushInput {
	
	private String id;

	public PushInput() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

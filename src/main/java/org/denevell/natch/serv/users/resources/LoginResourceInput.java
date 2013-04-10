package org.denevell.natch.serv.users.resources;

import javax.xml.bind.annotation.XmlRootElement;

import org.denevell.natch.serv.baseentities.UsernameAndPassword;

@XmlRootElement
public class LoginResourceInput extends UsernameAndPassword {

	public LoginResourceInput(String username, String password) {
		super(username, password);
	}
	
	public LoginResourceInput() {
	}
}

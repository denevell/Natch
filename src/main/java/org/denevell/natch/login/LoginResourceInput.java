package org.denevell.natch.login;

import javax.xml.bind.annotation.XmlRootElement;

import org.denevell.natch.rest.baseentities.UsernameAndPassword;

@XmlRootElement
public class LoginResourceInput extends UsernameAndPassword {

	public LoginResourceInput(String username, String password) {
		super(username, password);
	}
	
	public LoginResourceInput() {
	}
}

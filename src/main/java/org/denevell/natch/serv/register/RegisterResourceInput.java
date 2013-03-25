package org.denevell.natch.serv.register;

import javax.xml.bind.annotation.XmlRootElement;

import org.denevell.natch.rest.baseentities.UsernameAndPassword;

@XmlRootElement
public class RegisterResourceInput extends UsernameAndPassword {
	
	public RegisterResourceInput(String username, String password) {
		super(username, password);
	}
	
	public RegisterResourceInput() {
	}

}

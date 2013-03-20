package org.denevell.natch.register;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegisterResourceInput {
	
	private String username; 
	private String password;
	
	public RegisterResourceInput() {
	}

	public RegisterResourceInput(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// Should we do this here or in the model? Question!
//	public static boolean isValid(RegisterInput input) {
//		if(input.username!=null && input.username.trim().length()!=0 &&
//			input.password!=null && input.password.trim().length()!=0) {
//			return true;
//		} else {
//			return false;
//		}
//	}
}

package org.denevell.natch.serv.login.resources;

import javax.xml.bind.annotation.XmlRootElement;

import org.denevell.natch.serv.baseentities.SuccessOrError;

@XmlRootElement
public class LoginResourceReturnData extends SuccessOrError {
	private String authKey = "";

	public String getAuthKey() {
		return this.authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

}

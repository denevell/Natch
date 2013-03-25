package org.denevell.natch.serv.login;

import javax.xml.bind.annotation.XmlRootElement;

import org.denevell.natch.rest.baseentities.SuccessOrError;

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

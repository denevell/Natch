package org.denevell.natch.register;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegisterResourceReturnData {

	private boolean successful;

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}

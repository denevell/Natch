package org.denevell.natch.rest.output;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegisterReturnData {

	private boolean successful;

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}

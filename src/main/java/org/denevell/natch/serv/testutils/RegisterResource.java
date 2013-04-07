package org.denevell.natch.serv.testutils;

import java.util.ResourceBundle;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

import org.denevell.natch.utils.Strings;

@Path("testutils")
public class RegisterResource {
	
	private RegisterModel mUserModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public RegisterResource() {
		mUserModel = new RegisterModel();
	}
	
	@DELETE
	public void clearTestDb() {
		mUserModel.clearTestDb();
	}

}

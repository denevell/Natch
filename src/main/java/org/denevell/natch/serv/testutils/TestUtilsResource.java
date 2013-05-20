package org.denevell.natch.serv.testutils;

import java.util.ResourceBundle;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

import org.denevell.natch.utils.Strings;

@Path("testutils")
public class TestUtilsResource {
	
	private TestUtilsModel mUserModel;
    ResourceBundle rb = Strings.getMainResourceBundle();
	
	public TestUtilsResource() {
		mUserModel = new TestUtilsModel();
	}
	
	@DELETE
	public void clearTestDb() {
		mUserModel.clearTestDb();
	}

}

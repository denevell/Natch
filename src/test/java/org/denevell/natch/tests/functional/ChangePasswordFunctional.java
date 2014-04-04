package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.ChangePasswordPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class ChangePasswordFunctional {
	
	private WebTarget service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private RegisterPO registerPo;
	private ChangePasswordPO changePwPo;
	private LoginPO loginPo;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
	    registerPo = new RegisterPO(service);
	    loginPo = new LoginPO(service);
	    changePwPo = new ChangePasswordPO(service);
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldChangePassword() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    LoginResourceReturnData login = loginPo.login("aaron", "aaron");

	    // Act
	    Response response = changePwPo.change("newpass", login.getAuthKey());
	    assertEquals("204 response after change pass", 204, response.getStatus());
	    login = loginPo.login("aaron", "newpass");

		// Assert - Can login again
        assertTrue("Is successful", login.isSuccessful());

		// Act / Assert - Can't login with old password 
        try {
        	loginPo.login("aaron", "aaron");
        	assertTrue("Was able to login with old creds", false);
        } catch(WebApplicationException e) {
        	assertTrue("Unable to login with old creds", e.getResponse().getStatus()==403);
        }
	}

	// Tests can't when not logged in
	
}

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
import org.denevell.natch.tests.functional.pageobjects.LogoutPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class ChangePasswordAsAdminFunctional {
	
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
	    registerPo.register("aaron1", "aaron1");
	    LoginResourceReturnData login = loginPo.login("aaron", "aaron");

	    // Act
	    Response response = changePwPo.changeAsAdmin("aaron1", "newpass", login.getAuthKey());
	    assertEquals("204 response after change pass", 204, response.getStatus());

		// Assert - Can login again
	    login = loginPo.login("aaron1", "newpass");
        assertTrue("Is successful", login.isSuccessful());
        // Assert - Can login as other user too
	    login = loginPo.login("aaron", "aaron");
        assertTrue("Is successful", login.isSuccessful());

		// Act / Assert - Can't login with old password 
        try {
        	loginPo.login("aaron1", "aaron1");
        	assertTrue("Was able to login with old creds", false);
        } catch(WebApplicationException e) {
        	assertTrue("Unable to login with old creds", e.getResponse().getStatus()==403);
        }
	}

	@Test
	public void shouldntChangePasswordWithBlanks() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("aaron1", "aaron1");
	    LoginResourceReturnData login = loginPo.login("aaron", "aaron");

	    // Act
	    Response response = changePwPo.changeAsAdmin("aaron1", "       ", login.getAuthKey());
       	assertTrue("Unable to set password with blanks", response.getStatus()==400);
	}

	@Test
	public void shouldntChangePasswordWithBlank() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("aaron1", "aaron1");
	    LoginResourceReturnData login = loginPo.login("aaron", "aaron");

	    // Act
	    Response response = changePwPo.changeAsAdmin("aaron1", "", login.getAuthKey());
       	assertTrue("Unable to set password with blanks", response.getStatus()==400);
	}

	@Test
	public void shouldChangePasswordWhenLoggedOut() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("aaron1", "aaron1");
	    LoginResourceReturnData login = loginPo.login("aaron", "aaron");
	    new LogoutPO(service).logout(login.getAuthKey());

	    // Act
	    Response response = changePwPo.changeAsAdmin("aaron1", "newpass", login.getAuthKey());
       	assertTrue("Unable to set password with blanks", response.getStatus()==401);
	}
	
	// Not logged in

}

package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.ChangePasswordPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.LogoutPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class ChangePasswordFunctional {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private RegisterPO registerPo;
	private ChangePasswordPO changePwPo;
	private LoginPO loginPo;

	@Before
	public void setup() throws Exception {
	    registerPo = new RegisterPO();
	    loginPo = new LoginPO();
	    changePwPo = new ChangePasswordPO();
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

	@Test
	public void shouldntChangePasswordWhenNotLoggedIn() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    LoginResourceReturnData login = loginPo.login("aaron", "aaron");
	    new LogoutPO().logout(login.getAuthKey());

        Response r = changePwPo.change("newpass", login.getAuthKey());
       	assertEquals("Should see 401", 401, r.getStatus());
	}

	@Test
	public void shouldntChangePasswordToBlanks() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    LoginResourceReturnData login = loginPo.login("aaron", "aaron");

	    // Act
        Response r = changePwPo.change("      ", login.getAuthKey());
       	assertTrue("Wasnt able to login with old creds", r.getStatus()==400);
	}

	@Test
	public void shouldntChangePasswordToBlank() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    LoginResourceReturnData login = loginPo.login("aaron", "aaron");

	    // Act
        Response r = changePwPo.change("", login.getAuthKey());
       	assertTrue("Wasnt able to login with old creds", r.getStatus()==400);
	}
	
}

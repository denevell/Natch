package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.ListUsersPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.tests.functional.pageobjects.SetResetPasswordPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class ResetPasswordRequestFunctional {
	
	private WebTarget service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private RegisterPO registerPo;
	private SetResetPasswordPO resetPwRequest;
	private LoginPO loginPo;
	private ListUsersPO listUsersPo;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
	    registerPo = new RegisterPO(service);
	    loginPo = new LoginPO(service);
	    listUsersPo = new ListUsersPO(service);
	    resetPwRequest = new SetResetPasswordPO(service);
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldSetAndUnsetResetPasswordFlag() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("aaron1", "aaron1", "a@recover.com");
	    LoginResourceReturnData adminLogin = loginPo.login("aaron", "aaron");

	    // Act
	    Response response = resetPwRequest.setAsUser("a@recover.com");
	    
	    // Assert
	    assertEquals("204 response after request reset", 204, response.getStatus());
	    assertTrue("Reset pw set", listUsersPo.findUser("aaron1", adminLogin.getAuthKey()).isResetPasswordRequest());
	    
	    // Now deny unset since not admin
	    LoginResourceReturnData login = loginPo.login("aaron1", "aaron1");
	    response = resetPwRequest.unsetAsAdmin("aaron1", login.getAuthKey());
	    
	    // Assert
	    assertEquals("401 response after request un reset as normal user", 401, response.getStatus());
	    assertTrue("Reset pw set", listUsersPo.findUser("aaron1", adminLogin.getAuthKey()).isResetPasswordRequest());
	}

	@Test
	public void shouldUnsetResetPasswordAsAdmin() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("aaron1", "aaron1", "a@recover.com");
	    LoginResourceReturnData adminLogin = loginPo.login("aaron", "aaron");
	    resetPwRequest.setAsUser("a@recover.com");
	    assertTrue("Reset pw set", listUsersPo.findUser("aaron1", adminLogin.getAuthKey()).isResetPasswordRequest());

	    // Act
	    Response response = resetPwRequest.unsetAsAdmin("aaron1", adminLogin.getAuthKey());
	    
	    // Assert
	    assertEquals("204 response after request reset", 204, response.getStatus());
	    assertFalse("Reset pw set", listUsersPo.findUser("aaron1", adminLogin.getAuthKey()).isResetPasswordRequest());
	}

	
}

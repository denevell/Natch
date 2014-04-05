package org.denevell.natch.tests.functional;

import static org.junit.Assert.*;

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
	    LoginResourceReturnData login = loginPo.login("aaron", "aaron");

	    // Act
	    Response response = resetPwRequest.setAsUser(login.getAuthKey());
	    
	    // Assert
	    assertEquals("204 response after request reset", 204, response.getStatus());
	    assertTrue("Reset pw set", listUsersPo.listUsers(login.getAuthKey()).getUsers().get(0).isResetPasswordRequest());
	    
	    // Now unset
	    response = resetPwRequest.unsetAsUser(login.getAuthKey());
	    
	    // Assert
	    assertEquals("204 response after request reset", 204, response.getStatus());
	    assertFalse("Reset pw set", listUsersPo.listUsers(login.getAuthKey()).getUsers().get(0).isResetPasswordRequest());
	}

}

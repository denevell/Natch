package org.denevell.natch.tests.functional;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.User;
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
	    registerPo.register("aaron1", "aaron1");
	    LoginResourceReturnData login = loginPo.login("aaron1", "aaron1");
	    LoginResourceReturnData adminLogin = loginPo.login("aaron", "aaron");

	    // Act
	    Response response = resetPwRequest.setAsUser(login.getAuthKey());
	    
	    // Assert
	    assertEquals("204 response after request reset", 204, response.getStatus());
	    assertTrue("Reset pw set", getUserByName("aaron1", listUsersPo.listUsers(adminLogin.getAuthKey()).getUsers()).isResetPasswordRequest());
	    
	    // Now deny unset since not admin
	    response = resetPwRequest.unsetAsAdmin("aaron1", login.getAuthKey());
	    
	    // Assert
	    assertEquals("401 response after request un reset as normal user", 401, response.getStatus());
	    assertTrue("Reset pw set", getUserByName("aaron1", listUsersPo.listUsers(adminLogin.getAuthKey()).getUsers()).isResetPasswordRequest());
	}

	@Test
	public void shouldUnsetResetPasswordAsAdmin() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("aaron1", "aaron1");
	    LoginResourceReturnData login = loginPo.login("aaron1", "aaron1");
	    LoginResourceReturnData adminLogin = loginPo.login("aaron", "aaron");
	    resetPwRequest.setAsUser(login.getAuthKey());
	    assertTrue("Reset pw set", getUserByName("aaron1", listUsersPo.listUsers(adminLogin.getAuthKey()).getUsers()).isResetPasswordRequest());

	    // Act
	    Response response = resetPwRequest.unsetAsAdmin("aaron1", adminLogin.getAuthKey());
	    
	    // Assert
	    assertEquals("204 response after request reset", 204, response.getStatus());
	    assertFalse("Reset pw set", getUserByName("aaron1", listUsersPo.listUsers(adminLogin.getAuthKey()).getUsers()).isResetPasswordRequest());
	}
	
	public static User getUserByName(String name, List<User> users) {
		for (User user : users) {
			if(user.getUsername().equals(name)) {
				return user;
			}
		}
		return null;
	}
	
}

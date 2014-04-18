package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.User;
import org.denevell.natch.io.users.UserList;
import org.denevell.natch.tests.functional.pageobjects.ListUsersPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

public class UsersListFunctional {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private RegisterPO registerPo;
	private ListUsersPO listUsersPO;

	@Before
	public void setup() throws Exception {
	    registerPo = new RegisterPO();
	    listUsersPO = new ListUsersPO();
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldListUsersAsAdmin() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResult = new LoginPO().login("aaron", "aaron");

	    // Act
		UserList thread = listUsersPO.listUsers(loginResult.getAuthKey());	
		
		// Assert
		List<User> users = thread.getUsers();
		User user0 = listUsersPO.findUser("aaron", loginResult.getAuthKey());
		User user1 = listUsersPO.findUser("other1", loginResult.getAuthKey());
		assertEquals(2, users.size());
		assertEquals(true, user0.isAdmin());
		assertEquals(false, user1.isAdmin());
	}

    @Test
    public void shouldntListUsersAsNormalUser() {
        // Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResult = new LoginPO().login("other1", "other1");

        // Act
        try {
        	listUsersPO.listUsers(loginResult.getAuthKey());	
        } catch (WebApplicationException e) {
            assertEquals(401, e.getResponse().getStatus());
            return;
        }
        assertTrue("Was exception exception", false);
    }	

}

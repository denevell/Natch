package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.User;
import org.denevell.natch.io.users.UserList;
import org.denevell.natch.tests.functional.pageobjects.ListUsersPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class UsersListFunctional {
	
	private WebTarget service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private RegisterPO registerPo;
	private ListUsersPO listUsersPO;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
	    registerPo = new RegisterPO(service);
	    listUsersPO = new ListUsersPO(service);
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldListUsersAsAdmin() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron", "aaron");

	    // Act
		UserList thread = listUsers(service, loginResult.getAuthKey());	
		
		// Assert
		List<User> users = thread.getUsers();
		User user0 = listUsersPO.findUser("aaron", loginResult.getAuthKey());
		User user1 = listUsersPO.findUser("other1", loginResult.getAuthKey());
		assertEquals(2, users.size());
		assertEquals(true, user0.isAdmin());
		assertEquals(false, user1.isAdmin());
	}

    public static UserList listUsers(WebTarget s, String authKey) {
        UserList thread = s
        .path("rest").path("user").path("list").request()
        .header("AuthKey", authKey)
        .get(UserList.class);
        return thread;
    }
	
    @Test
    public void shouldntListUsersAsNormalUser() {
        // Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResult = new LoginPO(service).login("other1", "other1");

        // Act
        try {
            service
            .path("rest").path("user").path("list").request()
            .header("AuthKey", loginResult.getAuthKey())
            .get(UserList.class);   
        } catch (WebApplicationException e) {
            assertEquals(401, e.getResponse().getStatus());
            return;
        }
        assertTrue("Was exception exception", false);
    }	

}

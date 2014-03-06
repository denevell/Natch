package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.base.SuccessOrError;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.User;
import org.denevell.natch.io.users.UserList;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class UserAdminToggleFunctional {
	
	private WebTarget service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private RegisterPO registerPo;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
	    registerPo = new RegisterPO(service);
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldToggleUserAdmin() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron", "aaron");
		UserList users = UsersListFunctional.listUsers(service, loginResult.getAuthKey());
		User user = users.getUsers().get(1);
		if(!user.getUsername().equals("other1")) {
			user = users.getUsers().get(0);
			assertEquals("aaron", users.getUsers().get(1).getUsername());
		}
		assertEquals("other1", user.getUsername());
		assertEquals(false, users.getUsers().get(1).isAdmin());

	    // Act
        SuccessOrError result = toggleAdmin(loginResult);   

		// Assert
        assertTrue("Is successful", result.isSuccessful());
		users = UsersListFunctional.listUsers(service, loginResult.getAuthKey());
		user = users.getUsers().get(1);
		if(!user.getUsername().equals("other1")) {
			user = users.getUsers().get(0);
			assertEquals("aaron", users.getUsers().get(1).getUsername());
		}
		assertEquals("other1", user.getUsername());
		assertEquals(true, user.isAdmin());

	    // Act
        result = toggleAdmin(loginResult);   

		// Assert
        assertTrue("Is successful", result.isSuccessful());
		users = UsersListFunctional.listUsers(service, loginResult.getAuthKey());
		user = users.getUsers().get(1);
		if(!user.getUsername().equals("other1")) {
			user = users.getUsers().get(0);
			assertEquals("aaron", users.getUsers().get(1).getUsername());
		}
		assertEquals("other1", user.getUsername());
		assertEquals(false, user.isAdmin());
	}

	@Test
	public void shouldToggleWorksImmediately() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResultAdmin = new LoginPO(service).login("aaron", "aaron");
		LoginResourceReturnData loginResultUser = new LoginPO(service).login("other1", "other1");
		UserList users = UsersListFunctional.listUsers(service, loginResultAdmin.getAuthKey());
		User user = users.getUsers().get(1);
		if(!user.getUsername().equals("other1")) {
			user = users.getUsers().get(0);
			assertEquals("aaron", user.getUsername());
		}
		assertEquals("other1", user.getUsername());
		assertEquals(false, user.isAdmin());

	    // Act - make normal user an admin
        toggleAdmin(loginResultAdmin);   
	    // Act - now back to a non-admin
        toggleAdmin(loginResultAdmin);   

		// Assert - the normal user can run an admin commnad, i.e. toggle admin 
	    // Act
		try {
		    toggleAdmin(loginResultUser);   
        } catch (WebApplicationException e) {
            assertTrue("Get a 401 when not an admin", e.getResponse().getStatus()==401);
            return;
        }
		assertFalse("Was excepting an exception", true);
	}

	@Test
	public void shouldntToggleAdminIfNotAdmin() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResult = new LoginPO(service).login("other1", "other1");

	    // Act
		try {
		    toggleAdmin(loginResult);   
        } catch (WebApplicationException e) {
            assertTrue("Get a 401 when not an admin", e.getResponse().getStatus()==401);
            return;
        }
		assertFalse("Was excepting an exception", true);
	}

    public SuccessOrError toggleAdmin(LoginResourceReturnData loginResult) {
        SuccessOrError result = service
            .path("rest").path("user").path("admin").path("toggle").path("other1").request()
            .header("AuthKey", loginResult.getAuthKey())
            .post(Entity.entity(null, MediaType.APPLICATION_JSON), SuccessOrError.class);
        return result;
    }
	
}

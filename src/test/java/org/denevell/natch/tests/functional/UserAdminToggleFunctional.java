package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import org.denevell.natch.io.base.SuccessOrError;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.UserList;
import org.denevell.natch.tests.ui.pageobjects.LoginPO;
import org.denevell.natch.tests.ui.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class UserAdminToggleFunctional {
	
	private WebResource service;
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
		assertEquals("other1", users.getUsers().get(1).getUsername());
		assertEquals(false, users.getUsers().get(1).isAdmin());

	    // Act
        SuccessOrError result = toggleAdmin(loginResult);   

		// Assert
        assertTrue("Is successful", result.isSuccessful());
		users = UsersListFunctional.listUsers(service, loginResult.getAuthKey());
		assertEquals("other1", users.getUsers().get(1).getUsername());
		assertEquals(true, users.getUsers().get(1).isAdmin());

	    // Act
        result = toggleAdmin(loginResult);   

		// Assert
        assertTrue("Is successful", result.isSuccessful());
		users = UsersListFunctional.listUsers(service, loginResult.getAuthKey());
		assertEquals("other1", users.getUsers().get(1).getUsername());
		assertEquals(false, users.getUsers().get(1).isAdmin());
	}

	@Test
	public void shouldToggleWorksImmediately() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResultAdmin = new LoginPO(service).login("aaron", "aaron");
		LoginResourceReturnData loginResultUser = new LoginPO(service).login("other1", "other1");
		UserList users = UsersListFunctional.listUsers(service, loginResultAdmin.getAuthKey());
		assertEquals("other1", users.getUsers().get(1).getUsername());
		assertEquals(false, users.getUsers().get(1).isAdmin());

	    // Act - make normal user an admin
        toggleAdmin(loginResultAdmin);   
	    // Act - now back to a non-admin
        toggleAdmin(loginResultAdmin);   

		// Assert - the normal user can run an admin commnad, i.e. toggle admin 
	    // Act
		try {
		    toggleAdmin(loginResultUser);   
        } catch (UniformInterfaceException e) {
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
        } catch (UniformInterfaceException e) {
            assertTrue("Get a 401 when not an admin", e.getResponse().getStatus()==401);
            return;
        }
		assertFalse("Was excepting an exception", true);
	}

    public SuccessOrError toggleAdmin(LoginResourceReturnData loginResult) {
        SuccessOrError result = service
            .path("rest").path("user").path("admin").path("toggle").path("other1")
            .header("AuthKey", loginResult.getAuthKey())
            .post(SuccessOrError.class);
        return result;
    }
	
}
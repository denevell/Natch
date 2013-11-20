package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import org.denevell.natch.io.base.SuccessOrError;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.UserList;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class UserAdminToggleFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldToggleUserAdmin() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron", "aaron");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron", "aaron");
	    RegisterFunctional.register(service, registerInput);
	    RegisterFunctional.register(service, new RegisterResourceInput("other1", "other1"));
		LoginResourceReturnData loginResult = LoginFunctional.login(service, loginInput);
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
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron", "aaron");
	    ;
	    RegisterFunctional.register(service, registerInput);
	    RegisterFunctional.register(service, new RegisterResourceInput("other1", "other1"));
		LoginResourceReturnData loginResultAdmin = LoginFunctional.login(service, new LoginResourceInput("aaron", "aaron")); 
		LoginResourceReturnData loginResultUser = LoginFunctional.login(service, new LoginResourceInput("other1", "other1"));
		UserList users = UsersListFunctional.listUsers(service, loginResultAdmin.getAuthKey());
		assertEquals("other1", users.getUsers().get(1).getUsername());
		assertEquals(false, users.getUsers().get(1).isAdmin());

	    // Act - make normal user an admin
        SuccessOrError result = toggleAdmin(loginResultAdmin);   
	    // Act - now back to a non-admin
        result = toggleAdmin(loginResultAdmin);   

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
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron", "aaron");
	    RegisterFunctional.register(service, registerInput);
	    RegisterFunctional.register(service, new RegisterResourceInput("other1", "other1"));
	    LoginResourceInput loginInput = new LoginResourceInput("other1", "other1");
		LoginResourceReturnData loginResult = LoginFunctional.login(service, loginInput);

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

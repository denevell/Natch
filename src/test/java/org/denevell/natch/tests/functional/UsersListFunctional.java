package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

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

public class UsersListFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldListUsersAsAdmin() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron", "aaron");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron", "aaron");
	    RegisterFunctional.register(service, registerInput);
	    RegisterFunctional.register(service, new RegisterResourceInput("other1", "other1"));
		LoginResourceReturnData loginResult = LoginFunctional.login(service, loginInput);

	    // Act
		UserList thread = listUsers(service, loginResult.getAuthKey());	
		
		// Assert
		assertEquals(2, thread.getUsers().size());
		assertEquals("other1", thread.getUsers().get(1).getUsername());
		assertEquals(false, thread.getUsers().get(1).isAdmin());
		assertEquals("aaron", thread.getUsers().get(0).getUsername());
		assertEquals(true, thread.getUsers().get(0).isAdmin());
	}

    public static UserList listUsers(WebResource s, String authKey) {
        UserList thread = s
        .path("rest").path("user").path("list")
        .header("AuthKey", authKey)
        .get(UserList.class);
        return thread;
    }
	
    @Test
    public void shouldntListUsersAsNormalUser() {
        // Arrange 
        RegisterResourceInput registerInput = new RegisterResourceInput("aaron", "aaron");
        RegisterFunctional.register(service, registerInput);
        RegisterFunctional.register(service, new RegisterResourceInput("other1", "other1"));
        LoginResourceInput loginInput = new LoginResourceInput("other1", "other1");
        LoginResourceReturnData loginResult = LoginFunctional.login(service, loginInput);

        // Act
        try {
            service
            .path("rest").path("user").path("list")
            .header("AuthKey", loginResult.getAuthKey())
            .get(UserList.class);   
        } catch (UniformInterfaceException e) {
            assertEquals(401, e.getResponse().getStatus());
            return;
        }
        assertTrue("Was exception exception", false);
    }	

}

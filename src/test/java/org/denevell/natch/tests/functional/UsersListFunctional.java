package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.UserList;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class UsersListFunctional {
	
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
	public void shouldListUsersAsAdmin() {
		// Arrange 
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron", "aaron");

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
	    registerPo.register("aaron", "aaron");
	    registerPo.register("other1", "other1");
		LoginResourceReturnData loginResult = new LoginPO(service).login("other1", "other1");

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

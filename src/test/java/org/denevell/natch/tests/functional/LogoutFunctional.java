package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.LogoutResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class LogoutFunctional {
	
	private WebResource service;
	
	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldLogout() {
		// Arrange
	    new RegisterPO(service).register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
		
		// Act
		LogoutResourceReturnData logoutData = service
			.path("rest").path("user").path("logout")
			.header("AuthKey", loginResult.getAuthKey())
	    	.delete(LogoutResourceReturnData.class);		
		
		// Assert
		assertTrue(logoutData.isSuccessful());
	}
	
	@Test
	public void shouldntLogoutIfBadAuthData() {
		// Arrange
	    new RegisterPO(service).register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
		
		// Act
		try {
			service
				.path("rest").path("user").path("logout")
				.header("AuthKey", loginResult.getAuthKey()+"blar")
		    	.delete(LogoutResourceReturnData.class);		
			
		} catch(UniformInterfaceException e) {
			// Assert
			assertEquals(401, e.getResponse().getClientResponseStatus().getStatusCode());
			return;
		}
		assertFalse("Was excepting a 401 response", true);
		
	}
	
}

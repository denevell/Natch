package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.WebApplicationException;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.LogoutResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.LogoutPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.junit.Before;
import org.junit.Test;

public class LogoutFunctional {
	
	private LogoutPO logoutPo;
	
	@Before
	public void setup() throws Exception {
		logoutPo = new LogoutPO();
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldLogout() {
		// Arrange
	    new RegisterPO().register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO().login("aaron@aaron.com", "passy");
		
		// Act
		LogoutResourceReturnData logoutData = logoutPo.logout(loginResult.getAuthKey());
		
		// Assert
		assertTrue(logoutData.isSuccessful());
	}
	
	@Test
	public void shouldntLogoutIfBadAuthData() {
		// Arrange
	    new RegisterPO().register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO().login("aaron@aaron.com", "passy");
		
		// Act
		try {
			logoutPo.logout(loginResult.getAuthKey()+"blar");
		} catch(WebApplicationException e) {
			// Assert
			assertEquals(401, e.getResponse().getStatus());
			return;
		}
		assertFalse("Was excepting a 401 response", true);
		
	}
	
}

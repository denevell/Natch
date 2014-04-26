package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.WebApplicationException;

import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.LogoutResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.AddPostPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.LogoutPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.junit.Before;
import org.junit.Test;

public class LogoutFunctional {
	
	private LogoutPO logoutPo;
	private RegisterPO registerPo;
	private AddPostPO addPostPo;
	
	@Before
	public void setup() throws Exception {
		logoutPo = new LogoutPO();
		addPostPo = new AddPostPO();
		registerPo = new RegisterPO();
		TestUtils.deleteTestDb();
	}
	
	@Test
	public void shouldLogout() {
		// Arrange
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO().login("aaron@aaron.com", "passy");
		
		// Act
		LogoutResourceReturnData logoutData = logoutPo.logout(loginResult.getAuthKey());
		
		// Assert
		assertTrue(logoutData.isSuccessful());
	}
	
	@Test
	public void shouldntLogoutIfBadAuthData() {
		// Arrange
	    registerPo.register("aaron@aaron.com", "passy");
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

	@Test
	public void shouldLoginTwiceThenLogoutWithOneAndStillBeAbleToUseTheOther() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO().login("aaron@aaron.com", "passy");
		LoginResourceReturnData otherLoginResult = new LoginPO().login("aaron@aaron.com", "passy");
		logoutPo.logout(loginResult.getAuthKey());

	    // Act
		AddPostResourceReturnData result = addPostPo.add("s", "c", otherLoginResult.getAuthKey());
		assertTrue("Should be logged in to do this", result.isSuccessful());		
	}	
	
}

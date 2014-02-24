package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.users.LoginResourceLoggedInReturnData;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class LoginFunctional {
	
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
	public void shouldLoginWithGoodCredentials() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
		
		// Assert
		assertEquals("", loginResult.getError());
		assertTrue("Should return true as 'successful' field", loginResult.isSuccessful());
	}

	
	@Test
	public void shouldSeeJsonErrorOnBadCredentials() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passyWRONG");
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlanksPassed() {
		// Arrange 
		LoginResourceReturnData loginResult = new LoginPO(service).login(" ", " ");
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlankUsername() {
		// Arrange 
		LoginResourceReturnData loginResult = new LoginPO(service).login(" ", "password");
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlankPassword() {
		// Arrange 
		LoginResourceReturnData loginResult = new LoginPO(service).login("username", " ");
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnNullUsername() {
		// Arrange 
		LoginResourceReturnData loginResult = new LoginPO(service).login(null, "password");
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnNullPassword() {
		// Arrange 
		LoginResourceReturnData loginResult = new LoginPO(service).login("username", null);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBadJson() {
		// Deferred non functional requirement
	}
	
	@Test
	public void login_shouldBeAbleToLoginTwice() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult2 = new LoginPO(service).login("aaron@aaron.com", "passy");
		
		// Assert
		assertEquals("", loginResult.getError());
		assertTrue("Should return true as 'successful' field for first login", loginResult.isSuccessful());		
		assertEquals("", loginResult2.getError());
		assertTrue("Should return true as 'successful' field for second login", loginResult2.isSuccessful());		
	}
	
	@Test
	public void shouldReturnAuthKeyOnLogin() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
		
		// Assert
		assertTrue("Should return auth key", loginResult.getAuthKey().length()>5);
	}
	
	@Test
	public void shouldReturnDifferentAuthKeys() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult1 = new LoginPO(service).login("aaron@aaron.com", "passy");
		
		// Assert
		assertFalse("Should return different auth key", loginResult.getAuthKey().equals(loginResult1.getAuthKey()));		
	}
	
	@Test
	public void shouldLoginWithAuthKey() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
	    
	    // Act
		LoginResourceLoggedInReturnData authResult = service
	    		.path("rest").path("user").path("is")
	    		.type(MediaType.APPLICATION_JSON)
				.header("AuthKey", loginResult.getAuthKey())
	    		.get(LoginResourceLoggedInReturnData.class);
		
		// Assert
		assertTrue("Should say true to is logged in", authResult.isSuccessful());		
	}
	
	@Test
	public void shouldntLoginWithBadAuthKey() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");

		loginResult.getAuthKey();
	    
	    // Act
		try {
		service
	    	.path("rest").path("user").path("is")
	    	.type(MediaType.APPLICATION_JSON)
			.header("AuthKey", loginResult.getAuthKey()+"INCORRECT")
	    	.get(LoginResourceLoggedInReturnData.class);
		} catch(UniformInterfaceException e) {
			// Assert
			assertEquals("Should get 401", 401, e.getResponse().getClientResponseStatus().getStatusCode()); 
			return;
		}
		assertTrue("Wanted to see a 401", false);
	}
	
	@Test
	public void shouldntLoginWithOldAuthKey() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
		LoginResourceReturnData loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
		new LoginPO(service).login("aaron@aaron.com", "passy");
	    
	    // Act

		try {
			service
		    	.path("rest").path("user").path("is")
		    	.type(MediaType.APPLICATION_JSON)
				.header("AuthKey", loginResult.getAuthKey())
		    	.get(LoginResourceLoggedInReturnData.class);
		} catch(UniformInterfaceException e) {
			// Assert
			assertEquals("Should get 401", 401, e.getResponse().getClientResponseStatus().getStatusCode()); 
			return;
		}
		assertTrue("Wanted to see a 401", false);		
	}	
	
}

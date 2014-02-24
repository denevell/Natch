package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceLoggedInReturnData;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.ui.pageobjects.RegisterPO;
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
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    // Act
		LoginResourceReturnData loginResult = login(service, loginInput);
		
		// Assert
		assertEquals("", loginResult.getError());
		assertTrue("Should return true as 'successful' field", loginResult.isSuccessful());
	}

	
	@Test
	public void shouldSeeJsonErrorOnBadCredentials() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passyWRONG");
	    registerPo.register("aaron@aaron.com", "passy");
	    LoginResourceReturnData loginResult = login(service, loginInput);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlanksPassed() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput(" ", " ");
	    LoginResourceReturnData loginResult = login(service, loginInput);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlankUsername() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput(" ", "password");
	    LoginResourceReturnData loginResult = login(service, loginInput);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlankPassword() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput("username", " ");
	    LoginResourceReturnData loginResult = login(service, loginInput);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnNullUsername() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput(null, "password");
	    LoginResourceReturnData loginResult = login(service, loginInput);

		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnNullPassword() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput("username", null);
	    LoginResourceReturnData loginResult = login(service, loginInput);
		
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
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    
	    LoginResourceReturnData loginResult = login(service, loginInput);
	    LoginResourceReturnData loginResult2 = login(service, loginInput);
		
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
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    
	    LoginResourceReturnData loginResult = login(service, loginInput);
		
		// Assert
		assertTrue("Should return auth key", loginResult.getAuthKey().length()>5);
	}
	
	@Test
	public void shouldReturnDifferentAuthKeys() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    
	    LoginResourceReturnData loginResult = login(service, loginInput);
	    LoginResourceReturnData loginResult1 = login(service, loginInput);
		
		// Assert
		assertFalse("Should return different auth key", loginResult.getAuthKey().equals(loginResult1.getAuthKey()));		
	}
	
	@Test
	public void shouldLoginWithAuthKey() {
		// Arrange 
	    registerPo.register("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    LoginResourceReturnData loginResult = login(service, loginInput);
	    
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
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    LoginResourceReturnData loginResult = login(service, loginInput);

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
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    LoginResourceReturnData loginResult = login(service, loginInput);
	    login(service, loginInput);
	    
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
	

	public static LoginResourceReturnData login(WebResource service, LoginResourceInput loginInput) {
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		return loginResult;
	}	
	
}

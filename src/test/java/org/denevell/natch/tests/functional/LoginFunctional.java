package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceLoggedInReturnData;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class LoginFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();

	@Before
	public void setup() throws IOException, InterruptedException {
		service = TestUtils.getRESTClient();
		service
	    	.path("rest")
	    	.path("testutils")
	    	.delete();
	}
	
	@Test
	public void shouldLoginWithGoodCredentials() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals("", loginResult.getError());
		assertTrue("Should return true as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void shouldSeeJsonErrorOnBadCredentials() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passyWRONG");
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlanksPassed() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput(" ", " ");
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlankUsername() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput(" ", "password");
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlankPassword() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput("username", " ");
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnNullUsername() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput(null, "password");
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString(Strings.incorrect_username_or_password), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnNullPassword() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput("username", null);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
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
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		LoginResourceReturnData loginResult2 = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals("", loginResult.getError());
		assertTrue("Should return true as 'successful' field for first login", loginResult.isSuccessful());		
		assertEquals("", loginResult2.getError());
		assertTrue("Should return true as 'successful' field for second login", loginResult2.isSuccessful());		
	}
	
	@Test
	public void shouldReturnAuthKeyOnLogin() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertTrue("Should return auth key", loginResult.getAuthKey().length()>5);
	}
	
	@Test
	public void shouldReturnDifferentAuthKeys() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		LoginResourceReturnData loginResult1 = service
	    		.path("rest").path("user").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertFalse("Should return different auth key", loginResult.getAuthKey().equals(loginResult1.getAuthKey()));		
	}
	
	@Test
	public void shouldLoginWithAuthKey() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		LoginResourceReturnData loginResult = service
	    	.path("rest").path("user").path("login")
	    	.type(MediaType.APPLICATION_JSON)
	    	.post(LoginResourceReturnData.class, loginInput);
	    
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
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		LoginResourceReturnData loginResult = service
	    	.path("rest").path("user").path("login")
	    	.type(MediaType.APPLICATION_JSON)
	    	.post(LoginResourceReturnData.class, loginInput);
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
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		service
	    	.path("rest").path("user").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		LoginResourceReturnData loginResult = service
	    	.path("rest").path("user").path("login")
	    	.type(MediaType.APPLICATION_JSON)
	    	.post(LoginResourceReturnData.class, loginInput);
		service
	    	.path("rest").path("user").path("login")
	    	.type(MediaType.APPLICATION_JSON)
	    	.post(LoginResourceReturnData.class, loginInput);
	    
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

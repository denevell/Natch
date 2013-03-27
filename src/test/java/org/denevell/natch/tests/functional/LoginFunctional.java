package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.login.LoginResourceInput;
import org.denevell.natch.serv.login.LoginResourceLoggedInReturnData;
import org.denevell.natch.serv.login.LoginResourceReturnData;
import org.denevell.natch.serv.register.RegisterResourceInput;
import org.denevell.natch.serv.register.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class LoginFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();

	@Before
	public void setup() throws IOException, InterruptedException {
		service = TestUtils.getRESTClient();
		service
	    	.path("rest")
	    	.path("register")
	    	.delete();
	}
	
	@Test
	public void shouldLoginWithGoodCredentials() {
		// Arrange 
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		service
	    	.path("rest").path("register").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
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
	    	.path("rest").path("register").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString("incorrect_username"), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlanksPassed() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput(" ", " ");
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString("incorrect_username"), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlankUsername() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput(" ", "password");
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString("incorrect_username"), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnBlankPassword() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput("username", " ");
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString("incorrect_username"), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnNullUsername() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput(null, "password");
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString("incorrect_username"), loginResult.getError());
		assertFalse("Should return false as 'successful' field", loginResult.isSuccessful());
	}
	
	@Test
	public void login_shouldSeeJsonErrorOnNullPassword() {
		// Arrange 
	    LoginResourceInput loginInput = new LoginResourceInput("username", null);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		
		// Assert
		assertEquals(rb.getString("incorrect_username"), loginResult.getError());
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
	    	.path("rest").path("register").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		LoginResourceReturnData loginResult2 = service
	    		.path("rest").path("login")
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
	    	.path("rest").path("register").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
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
	    	.path("rest").path("register").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
	    
	    // Act
		LoginResourceReturnData loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);
		LoginResourceReturnData loginResult1 = service
	    		.path("rest").path("login")
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
	    	.path("rest").path("register").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		LoginResourceReturnData loginResult = service
	    	.path("rest").path("login")
	    	.type(MediaType.APPLICATION_JSON)
	    	.post(LoginResourceReturnData.class, loginInput);
		String authKey = loginResult.getAuthKey();
	    
	    // Act
		LoginResourceLoggedInReturnData authResult = service
	    		.path("rest").path("login").path("is").path(authKey)
	    		.type(MediaType.APPLICATION_JSON)
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
	    	.path("rest").path("register").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		LoginResourceReturnData loginResult = service
	    	.path("rest").path("login")
	    	.type(MediaType.APPLICATION_JSON)
	    	.post(LoginResourceReturnData.class, loginInput);
		String authKey = loginResult.getAuthKey();
	    
	    // Act
		LoginResourceLoggedInReturnData authResult = service
	    		.path("rest").path("login").path("is").path(authKey+"INCORRECT")
	    		.type(MediaType.APPLICATION_JSON)
	    		.get(LoginResourceLoggedInReturnData.class);
		
		// Assert
		assertFalse("Should say true to is logged in", authResult.isSuccessful());		
	}
	
}

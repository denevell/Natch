package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.users.resources.LoginResourceInput;
import org.denevell.natch.serv.users.resources.LoginResourceReturnData;
import org.denevell.natch.serv.users.resources.LogoutResourceReturnData;
import org.denevell.natch.serv.users.resources.RegisterResourceInput;
import org.denevell.natch.serv.users.resources.RegisterResourceReturnData;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class LogoutFunctional {
	
	private WebResource service;
	
	@Before
	public void setup() throws IOException, InterruptedException {
		service = TestUtils.getRESTClient();
		service
	    	.path("rest")
	    	.path("testutils")
	    	.delete();
	}
	
	@Test
	public void shouldLogout() {
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

package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.login.LoginResourceInput;
import org.denevell.natch.serv.login.LoginResourceReturnData;
import org.denevell.natch.serv.posts.AddPostResourceInput;
import org.denevell.natch.serv.posts.AddPostResourceReturnData;
import org.denevell.natch.serv.register.RegisterResourceInput;
import org.denevell.natch.serv.register.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class AddPostsFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;
	
	@Before
	public void setup() {
		service = TestUtils.getRESTClient();
		// Delete all users
		service
	    	.path("rest")
	    	.path("register")
	    	.delete();	
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    // Register
		service
	    	.path("rest").path("register").type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		loginResult = service
	    		.path("rest").path("login")
	    		.type(MediaType.APPLICATION_JSON)
	    		.post(LoginResourceReturnData.class, loginInput);		
	}
	
	@Test
	public void shouldMakePost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		
		// Act
		AddPostResourceReturnData returnData = service
		.path("rest").path("post")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Assert
		assertTrue(returnData.isSuccessful());
		assertEquals("", returnData.getError());
	}
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
	}
	
	@Test
	public void shouldSeeErrorOnBlankSubject() {
	}
	
	@Test
	public void shouldSeeErrorOnBlankContent() {
	}
	
	@Test
	public void shouldSeeErrorOnBlanks() {
	}
}

package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import com.sun.jersey.api.client.UniformInterfaceException;
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
		assertEquals("", returnData.getError());
		assertTrue(returnData.isSuccessful());
	}
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		
		// Act
		try {
			service
				.path("rest").path("post")
			    .type(MediaType.APPLICATION_JSON)
				.header("AuthKey", loginResult.getAuthKey()+"BAD")
		    	.put(AddPostResourceReturnData.class, input); 
		} catch(UniformInterfaceException e) {
			// Assert
			assertEquals(401, e.getResponse().getClientResponseStatus().getStatusCode());
			return;
		}
		assertFalse("Was excepting a 401 response", true);		
	}
	
	@Test
	public void shouldSeeErrorOnBlankSubject() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput(" ", "cont");
		
		// Act
		AddPostResourceReturnData returnData = service
		.path("rest").path("post")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
	
	@Test
	public void shouldSeeErrorOnBlankContent() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", " ");
		
		// Act
		AddPostResourceReturnData returnData = service
		.path("rest").path("post")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
	
	@Test
	public void shouldSeeErrorOnBlanks() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput(" ", " ");
		
		// Act
		AddPostResourceReturnData returnData = service
		.path("rest").path("post")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
	
	@Test
	public void shouldSeeErrorOnNulls() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput(null,null);
		
		// Act
		AddPostResourceReturnData returnData = service
		.path("rest").path("post")
	    .type(MediaType.APPLICATION_JSON)
		.header("AuthKey", loginResult.getAuthKey())
    	.put(AddPostResourceReturnData.class, input); 
		
		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
}

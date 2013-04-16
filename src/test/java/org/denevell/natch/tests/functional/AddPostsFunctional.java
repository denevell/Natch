package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.serv.posts.resources.AddPostResourceInput;
import org.denevell.natch.serv.posts.resources.AddPostResourceReturnData;
import org.denevell.natch.serv.users.resources.LoginResourceInput;
import org.denevell.natch.serv.users.resources.LoginResourceReturnData;
import org.denevell.natch.serv.users.resources.RegisterResourceInput;
import org.denevell.natch.serv.users.resources.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class AddPostsFunctional {
	
	private WebResource addPostService;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;
	
	@Before
	public void setup() {
		addPostService = TestUtils.getAddPostClient();
		TestUtils.deleteAllDbs();
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
		// Register
		TestUtils.getRegisterClient()
		.type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);
		// Login
		LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
		loginResult = TestUtils.getLoginClient()
		.type(MediaType.APPLICATION_JSON)
	    	.post(LoginResourceReturnData.class, loginInput);		
	}
	
	@Test
	public void shouldMakePost() {
		// Arrange 
		List<String> tags = Arrays.asList("tag1", "tag2");
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", tags);
		
		// Act
		AddPostResourceReturnData returnData = 
		addPostService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
		.put(AddPostResourceReturnData.class, input); 
		
		// Assert
		assertEquals("", returnData.getError());
		assertTrue(returnData.isSuccessful());
	}
	
	@Test public void shouldMakePostWithSameContentAndSubject() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		
		// Act
		AddPostResourceReturnData returnData = 
		addPostService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
		.put(AddPostResourceReturnData.class, input); 
		AddPostResourceReturnData returnData1 = 
		addPostService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
		.put(AddPostResourceReturnData.class, input); 
		
		// Assert
		assertEquals("", returnData.getError());
		assertEquals("", returnData1.getError());
		assertTrue(returnData.isSuccessful());
		assertTrue(returnData1.isSuccessful());
	}
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		
		// Act
		try {
			addPostService
			.header("AuthKey", loginResult.getAuthKey()+"BAD")
			.type(MediaType.APPLICATION_JSON)
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
		AddPostResourceReturnData returnData = 
		addPostService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
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
		AddPostResourceReturnData returnData = 
		addPostService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
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
		AddPostResourceReturnData returnData = 
		addPostService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
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
		AddPostResourceReturnData returnData = 
		addPostService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
		.put(AddPostResourceReturnData.class, input); 
		
		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
}

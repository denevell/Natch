package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.threads.AddThreadResourceInput;
import org.denevell.natch.io.threads.AddThreadResourceReturnData;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class AddThreadFunctional {
	
	private WebResource addThreadService;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;
	
	@Before
	public void setup() throws Exception {
		addThreadService = TestUtils.getAddThreadClient();
		TestUtils.deleteTestDb();
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
	public void shouldMakeThread() {
		// Arrange 
		List<String> tags = Arrays.asList("tag1", "tag2");
		AddThreadResourceInput input = new AddThreadResourceInput("sub", "cont", tags);
		
		// Act
		AddThreadResourceReturnData returnData = 
		addThreadService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
		.put(AddThreadResourceReturnData.class, input); 
		
		// Assert
		assertEquals("", returnData.getError());
		assertTrue("Thraed id > 0", returnData.getThreadId()>0);
		assertTrue(returnData.isSuccessful());
	}
	

	@Test 
	public void shouldMakePostWithLongPost() {
		// Arrange 
		AddThreadResourceInput input = new AddThreadResourceInput("sub", 
				"Lorem ipsum dolor sit amet, consectetur adipisicing elit," +
				"sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
				"Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip" +
				"ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit" +
				"esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
				"sunt in culpa qui officia deserunt mollit anim id est laborum. 	Lorem ipsum dolor sit " +
				"amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore " +
				"magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
				"aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
				"esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident," +
				" sunt in culpa qui officia deserunt mollit anim id est laborum. 	Lorem ipsum dolor sit " +
				"amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore " +
				"magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
				"aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
				"esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
				"sunt in culpa qui officia deserunt mollit anim id est laborum.");
		
		// Act
		AddThreadResourceReturnData returnData = 
		addThreadService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
		.put(AddThreadResourceReturnData.class, input); 
		
		// Assert
		assertEquals("", returnData.getError());
		assertTrue("Thraed id > 0", returnData.getThreadId()>0);
		assertTrue(returnData.isSuccessful());		
	}	

	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		// Arrange 
		List<String> tags = Arrays.asList("tag1", "tag2");
		AddThreadResourceInput input = new AddThreadResourceInput("sub", "cont", tags);
		
		// Act / Assert
		try {
			addThreadService
			.header("AuthKey", loginResult.getAuthKey()+"BAD")
			.type(MediaType.APPLICATION_JSON)
			.put(AddThreadResourceReturnData.class, input); 
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
		List<String> tags = Arrays.asList("tag1", "tag2");
		AddThreadResourceInput input = new AddThreadResourceInput(" ", "cont", tags);
		
		// Act
		AddThreadResourceReturnData returnData = 
		addThreadService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
		.put(AddThreadResourceReturnData.class, input); 
		
		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
	
	@Test
	public void shouldSeeErrorOnBlankContent() {
		// Arrange 
		List<String> tags = Arrays.asList("tag1", "tag2");
		AddThreadResourceInput input = new AddThreadResourceInput("sub", " ", tags);
		
		// Act
		AddThreadResourceReturnData returnData = 
		addThreadService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
		.put(AddThreadResourceReturnData.class, input); 
		
		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
	
	@Test
	public void shouldSeeErrorOnNulls() {
		// Arrange 
		List<String> tags = Arrays.asList("tag1", "tag2");
		AddThreadResourceInput input = new AddThreadResourceInput(null, null, tags);
		
		// Act
		AddThreadResourceReturnData returnData = 
		addThreadService
		.header("AuthKey", loginResult.getAuthKey())
		.type(MediaType.APPLICATION_JSON)
		.put(AddThreadResourceReturnData.class, input); 
		
		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
	

	public static AddThreadResourceReturnData addThread(
			AddThreadResourceInput input, 
			WebResource service, 
			String authKey) {
		return service
			.path("rest").path("threads").path("add")
		    .type(MediaType.APPLICATION_JSON)
			.header("AuthKey", authKey)
	    	.put(AddThreadResourceReturnData.class, input);
	}			
}

package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
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

public class AddPostsFunctional {
	
	private WebResource addPostService;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;
	private AddThreadResourceReturnData thread;
	
	@Before
	public void setup() throws Exception {
		addPostService = TestUtils.getAddPostClient();
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
		// Add thread
		AddThreadResourceInput threadInput = new AddThreadResourceInput("s", "c");
		thread = AddThreadFunctional.addThread(threadInput, TestUtils.getRESTClient(), loginResult.getAuthKey());
	}
	
	@Test
	public void shouldMakePost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("post content", 
				thread.getThreadId());
		
		// Act
		AddPostResourceReturnData returnData = addPost(input, 
				TestUtils.getRESTClient(), 
				loginResult.getAuthKey()); 
		
		// Assert
		assertEquals("", returnData.getError());
		assertTrue(returnData.isSuccessful());
		assertEquals("Post id should be -1 every time", -1, returnData.getId());
	}

	public static AddPostResourceReturnData addPost(AddPostResourceInput input, WebResource service, Object authKey) {
		AddPostResourceReturnData returnData = 
			service
			.path("rest").path("post").path("add")			
			.header("AuthKey", authKey)
			.type(MediaType.APPLICATION_JSON)
			.put(AddPostResourceReturnData.class, input);
		return returnData;
	}
	
	@Test
	public void shouldMakePostWithLongPost() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("Lorem ipsum dolor sit amet, consectetur adipisicing elit," +
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
				"sunt in culpa qui officia deserunt mollit anim id est laborum.", thread.getThreadId());
		
		// Act
		AddPostResourceReturnData returnData = addPost(input, 
				TestUtils.getRESTClient(), 
				loginResult.getAuthKey());		

		// Assert
		assertEquals("", returnData.getError());
		assertTrue(returnData.isSuccessful());
	}	
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("asdf", 
				thread.getThreadId());
		
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
	public void shouldSeeErrorOnBlankContent() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput(" ", 
				thread.getThreadId());
		
		// Act
		AddPostResourceReturnData returnData = addPost(input, 
				TestUtils.getRESTClient(), 
				loginResult.getAuthKey());				

		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
	
	@Test
	public void shouldSeeErrorOnNulls() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput(null, 
				thread.getThreadId());
		
		// Act
		AddPostResourceReturnData returnData = addPost(input, 
				TestUtils.getRESTClient(), 
				loginResult.getAuthKey());	
		
		// Assert
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
		assertFalse(returnData.isSuccessful());
	}
}

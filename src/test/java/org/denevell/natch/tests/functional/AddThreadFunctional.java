package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.posts.EditPostResource;
import org.denevell.natch.io.posts.EditPostResourceReturnData;
import org.denevell.natch.io.posts.ListPostsResource;
import org.denevell.natch.io.posts.PostResource;
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
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;
	
	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
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
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont", tags);
		
		// Act
		AddPostResourceReturnData returnData = addThread(service, loginResult.getAuthKey(), input); 
		
		// Assert
		assertEquals("", returnData.getError());
		assertNotNull(returnData.getThread().getSubject());
		assertTrue(returnData.isSuccessful());
	}
	
    @SuppressWarnings("serial")
    @Test
    public void shouldSeeErrorOnLargeTag() {
        // Arrange
        AddPostResourceInput threadInput = new AddPostResourceInput("thread", "threadc");
        threadInput.setTags(new ArrayList<String>(){{
           add("small");
           add("thisislargerthan20charactersdefiniteily");
        }});
        
        // Act - edit then list
		AddPostResourceReturnData returnData = addThread(service, loginResult.getAuthKey(), threadInput); 
        
        // Assert
        assertFalse(returnData.isSuccessful());     
        assertEquals(rb.getString(Strings.tag_too_large), returnData.getError());
    }	
	
	@Test
	public void shouldSeeErrorOnBlankSubject() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput(" ", "cont");
		
		// Act
		AddPostResourceReturnData returnData = addThread(service, loginResult.getAuthKey(), input);
		
		// Assert
		assertFalse(returnData.isSuccessful());
		assertEquals(rb.getString(Strings.post_fields_cannot_be_blank), returnData.getError());
	}

	@Test
	public void shouldSeeAddThreadErrorOnUnAuthorised() {
		// Arrange 
		AddPostResourceInput input = new AddPostResourceInput("sub", "cont");
		
		// Act
		try {
		    addThread(service, loginResult.getAuthKey()+"BAD", input);
		} catch(UniformInterfaceException e) {
			// Assert
			assertEquals(401, e.getResponse().getClientResponseStatus().getStatusCode());
			return;
		}
		assertFalse("Was excepting a 401 response", true);		
	}	

    public static AddPostResourceReturnData addThread(WebResource service, Object authKey, AddPostResourceInput input) {
        AddPostResourceReturnData returnData = 
        service
        .path("rest").path("post").path("addthread")
		.header("AuthKey", authKey)
		.type(MediaType.APPLICATION_JSON)
		.put(AddPostResourceReturnData.class, input);
        return returnData;
    }
	
}

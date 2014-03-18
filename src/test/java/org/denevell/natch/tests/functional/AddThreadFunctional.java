package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.io.posts.AddPostResourceInput;
import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class AddThreadFunctional {
	
	private WebTarget service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;
	
	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	    new RegisterPO(service).register("aaron@aaron.com", "passy");
		// Login
		loginResult = new LoginPO(service).login("aaron@aaron.com", "passy");
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
		assertTrue("Has tags in returned value",  returnData.getThread().getTags()!=null && returnData.getThread().getTags().contains("tag2"));
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
    public void shouldSeeErrorOnLargeSubject() {
        // Arrange
        AddPostResourceInput threadInput = new AddPostResourceInput("threadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthread", "threadc");
        
        // Act - edit then list
		AddPostResourceReturnData returnData = addThread(service, loginResult.getAuthKey(), threadInput); 
        
        // Assert
        assertFalse(returnData.isSuccessful());     
        assertEquals(rb.getString(Strings.subject_too_large), returnData.getError());
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
		} catch(WebApplicationException e) {
			// Assert
			assertEquals(401, e.getResponse().getStatus());
			return;
		}
		assertFalse("Was excepting a 401 response", true);		
	}	

    public static AddPostResourceReturnData addThread(WebTarget service, Object authKey, AddPostResourceInput input) {
        AddPostResourceReturnData returnData = 
        service
        .path("rest").path("post").path("addthread").request()
		.header("AuthKey", authKey)
		.put(Entity.entity(input, MediaType.APPLICATION_JSON), AddPostResourceReturnData.class);
        return returnData;
    }
	
}

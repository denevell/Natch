package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.model.ThreadEntity.Output;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;


public class ThreadAddFunctional {
	
	private LoginResourceReturnData loginResult;
  private ThreadsListPO threadsListPo;
  private ThreadAddPO threadAddPo;
	
	@Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		threadAddPo = new ThreadAddPO();
		threadsListPo = new ThreadsListPO();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	}
	
	@Test
	public void shouldMakeThread() {
	  Response addResponse = threadAddPo.add("sub", "cont", Lists.newArrayList("tagy"), loginResult.getAuthKey());
	  Output thread = threadsListPo.list(0,  10).threads.get(0);
		
		assertEquals(200, addResponse.getStatus());
		assertEquals("sub", thread.subject);
		assertEquals("tagy", thread.tags.get(0));
	}

  /*
	
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
		AddPostResourceInput input = new AddPostResourceInput(" ", "cont");
		try {
			addThread(service, loginResult.getAuthKey(), input);
		} catch(WebApplicationException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		assertFalse("Was excepting a 400 response", true);		
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
    */
	
}

package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.WebApplicationException;

import org.denevell.natch.io.posts.AddPostResourceReturnData;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.AddPostPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.junit.Before;
import org.junit.Test;

public class AddPostFunctional {
	
	private LoginResourceReturnData loginResult;
    private String authKey;
	private AddPostPO addPostPo;
	
	@Before
	public void setup() throws Exception {
		addPostPo = new AddPostPO();
		TestUtils.deleteTestDb();
	    new RegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new LoginPO().login("aaron@aaron.com", "passy");
		authKey = loginResult.getAuthKey();
	}
	
	@Test
	public void shouldMakePost() {
		AddPostResourceReturnData returnData = 
				addPostPo.add("sub", "cont", new String[] {"tag1", "tag2"}, authKey);
		
		// Assert
		assertEquals("", returnData.getError());
		assertNotNull(returnData.getThread().getSubject());
		assertTrue(returnData.isSuccessful());
	}

	@Test public void shouldMakePostWithSameContentAndSubject() {
		AddPostResourceReturnData returnData = 
				addPostPo.add("sub", "cont", new String[] {"tag1", "tag2"}, authKey);
		AddPostResourceReturnData returnData1 = 
				addPostPo.add("sub", "cont", new String[] {"tag1", "tag2"}, authKey);
		
		// Assert
		assertEquals("", returnData.getError());
		assertEquals("", returnData1.getError());
		assertTrue(returnData.isSuccessful());
		assertTrue(returnData1.isSuccessful());
	}
	
	@Test 
	public void shouldMakePostWithLongPost() {
		String largeContent = "Lorem ipsum dolor sit amet, consectetur adipisicing elit," +
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
				"sunt in culpa qui officia deserunt mollit anim id est laborum.";
		AddPostResourceReturnData returnData = 
				addPostPo.add("sub", largeContent, new String[] {"tag1", "tag2"}, authKey);
		AddPostResourceReturnData returnData1 = 
				addPostPo.add("sub", largeContent, new String[] {"tag1", "tag2"}, authKey);
		
		// Assert
		assertEquals("", returnData.getError());
		assertEquals("", returnData1.getError());
		assertTrue(returnData.isSuccessful());
		assertTrue(returnData1.isSuccessful());
	}	
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		try {
			addPostPo.add("sub", "cont", new String[] {"tag1", "tag2"}, authKey+"BAD");
		} catch(WebApplicationException e) {
			// Assert
			assertEquals(401, e.getResponse().getStatus());
			return;
		}
		assertFalse("Was excepting a 401 response", true);		
	}
	
	@Test
	public void shouldSeeErrorOnBlankContent() {
		try {
			addPostPo.add("sub", " ", authKey);
		} catch(WebApplicationException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
	}
	
	@Test
	public void shouldSeeErrorOnBlanks() {
		try {
			addPostPo.add(" ", " ", authKey);
		} catch(WebApplicationException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		assertFalse("Was excepting a 400 response", true);		
	}
	
	@Test
	public void shouldSeeErrorOnNulls() {
		try {
			addPostPo.add(null, null, authKey);
		} catch(WebApplicationException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		assertFalse("Was excepting a 400 response", true);			
	}
	
}

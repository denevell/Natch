package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.denevell.natch.tests.functional.pageobjects.AddPostPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;

public class PostAddFunctional {
	
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
		assertEquals("200", addPostPo.add("cont", authKey, "thread"));
	}

	@Test 
	public void shouldMakePostWithSameContent() {
		assertEquals("200", addPostPo.add("cont", authKey, "thread"));
		assertEquals("200", addPostPo.add("cont", authKey, "thread"));
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
		assertEquals("200", addPostPo.add(largeContent, authKey, "thread"));
	}	
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		assertEquals("401", addPostPo.add("cont", authKey+"BAD", "thread"));
	}
	
	@Test
	public void shouldSeeErrorOnBlankContent() {
		Response response = addPostPo.add(" ", authKey+"BAD", "thread");
    assertEquals("400", response);
	}

	@Test
	public void shouldSeeErrorOnBlankThread() {
		Response response = addPostPo.add("cont", authKey, " ");
    assertEquals("400", response);
	}
	
	
	@Test
	public void shouldSeeErrorOnBlanks() {
		Response response = addPostPo.add(" ", authKey, " ");
    assertEquals("400", response);
	}

	@Test
	public void shouldSeeErrorOnNulls() {
		Response response = addPostPo.add(null, authKey, null);
    assertEquals("400", response);
	}
}

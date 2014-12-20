package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.entities.ThreadEntity.Output;
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
		assertEquals("Excepting 200 response", 200, addResponse.getStatus());
	  Output thread = threadsListPo.list(0,  10).results.get(0);
		
		assertEquals("sub", thread.subject);
		assertEquals("tagy", thread.tags.get(0));
	}
	
  @Test
  public void shouldSeeErrorOnLargeTag() {
	  Response addResponse = threadAddPo.add("sub", "cont", 
	      Lists.newArrayList("thisislargerthan20charactersdefiniteily"), loginResult.getAuthKey());

		assertEquals(400, addResponse.getStatus());
		assertEquals("Tag cannot be more than 20 characters", TestUtils.getValidationMessage(addResponse, 0));
  }

  @Test
  public void shouldSeeErrorOnLargeSubject() {
	  Response addResponse = threadAddPo.add("threadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthreadthread", 
	      "cont", loginResult.getAuthKey());

		assertEquals(400, addResponse.getStatus());
		assertEquals("Subject cannot be more than 300 characters", TestUtils.getValidationMessage(addResponse, 0));
  }

	@Test
	public void shouldSeeErrorOnBlankSubject() {
	  Response addResponse = threadAddPo.add(" ", "cont", loginResult.getAuthKey());

		assertEquals(400, addResponse.getStatus());
		assertEquals("Subject cannot be blank", TestUtils.getValidationMessage(addResponse, 0));
	}

	@Test
	public void shouldSeeErrorOnBlankContent() {
	  Response addResponse = threadAddPo.add("sub", " ", loginResult.getAuthKey());

		assertEquals(400, addResponse.getStatus());
		assertEquals("Content cannot be blank", TestUtils.getValidationMessage(addResponse, 0));
	}

	@Test
	public void shouldSeeAddThreadErrorOnUnAuthorised() {
	  Response addResponse = threadAddPo.add("sub", "cont", loginResult.getAuthKey()+"BAD");

		assertEquals(401, addResponse.getStatus());
	}
	
}

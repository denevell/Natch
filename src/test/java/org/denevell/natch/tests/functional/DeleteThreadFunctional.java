package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import org.denevell.natch.io.threads.AddThreadResourceInput;
import org.denevell.natch.io.threads.DeleteThreadResourceReturnData;
import org.denevell.natch.io.threads.ThreadsResource;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class DeleteThreadFunctional {
	
	private WebResource service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData loginResult;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	    // Register
	    RegisterResourceInput registerInput = new RegisterResourceInput("aaron@aaron.com", "passy");
	    RegisterFunctional.register(registerInput, service);
		// Login
	    LoginResourceInput loginInput = new LoginResourceInput("aaron@aaron.com", "passy");
	    loginResult = LoginFunctional.login(loginInput, service);
	}
	
	@Test
	public void shouldDeleteThread() {
		// Arrange 
		AddThreadResourceInput input = new AddThreadResourceInput("a", "a");
		AddThreadResourceInput input1 = new AddThreadResourceInput("b", "b");;
		AddThreadFunctional.addThread(input, service, loginResult.getAuthKey());
		AddThreadFunctional.addThread(input1, service, loginResult.getAuthKey());
		ThreadsResource listThreads = ListThreadsFunctional.listTenThreads(service); 
		
		// Act
		DeleteThreadResourceReturnData ret = service.path("rest").path("threads").path("del")
		.path(String.valueOf(listThreads.getThreads().get(0).getId()))
		.header("AuthKey", loginResult.getAuthKey())
		.entity(null)
		.delete(DeleteThreadResourceReturnData.class);
		ThreadsResource listThreadsAfter = ListThreadsFunctional.listTenThreads(service);
		
		// Assert
		assertEquals("", ret.getError());
		assertTrue(ret.isSuccessful());		
		assertEquals(2, listThreads.getThreads().size());		
		assertEquals(1, listThreadsAfter.getThreads().size());		
		assertEquals("a", listThreadsAfter.getThreads().get(0).getSubject());
	}
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		// Arrange 
		// Register other user
	    LoginResourceInput loginInput1 = new LoginResourceInput("aaron1@aaron.com", "passy");
	    RegisterFunctional.register(loginInput1, service);
	    LoginResourceReturnData loginResult1 = LoginFunctional.login(loginInput1, service);
	    // Add threads with new user
		AddThreadResourceInput input = new AddThreadResourceInput("a", "a");
		AddThreadFunctional.addThread(input, service, loginResult1.getAuthKey());
		ThreadsResource listThreads = ListThreadsFunctional.listTenThreads(service); 
		
		// Act - Delete with initial user
		DeleteThreadResourceReturnData ret = service.path("rest").path("threads").path("del")
		.path(String.valueOf(listThreads.getThreads().get(0).getId()))
		.header("AuthKey", loginResult.getAuthKey())
		.entity(null)
		.delete(DeleteThreadResourceReturnData.class);
		ThreadsResource listThreadsAfter = ListThreadsFunctional.listTenThreads(service);		

		// Assert
		assertEquals(rb.getString(Strings.post_not_yours), ret.getError());
		assertFalse(ret.isSuccessful());		
		assertEquals(1, listThreads.getThreads().size());		
		assertEquals(1, listThreadsAfter.getThreads().size());		
	}
	
	@Test
	public void shouldSeeErrorOnUnknownThread() {
		// Arrange 
		AddThreadResourceInput input1 = new AddThreadResourceInput("b", "b");;
		AddThreadFunctional.addThread(input1, service, loginResult.getAuthKey());
		ThreadsResource listThreads = ListThreadsFunctional.listTenThreads(service); 
		
		// Act
		DeleteThreadResourceReturnData ret = service.path("rest").path("threads").path("del")
		.path(String.valueOf(listThreads.getThreads().get(0).getId()+"34"))
		.header("AuthKey", loginResult.getAuthKey())
		.entity(null)
		.delete(DeleteThreadResourceReturnData.class);
		ThreadsResource listThreadsAfter = ListThreadsFunctional.listTenThreads(service);
		
		// Assert
		assertEquals(rb.getString(Strings.post_doesnt_exist), ret.getError());
		assertFalse(ret.isSuccessful());		
		assertEquals(1, listThreads.getThreads().size());		
		assertEquals(1, listThreadsAfter.getThreads().size());				
	}
	
}

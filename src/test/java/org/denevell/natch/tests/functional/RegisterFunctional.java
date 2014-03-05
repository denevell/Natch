package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;

public class RegisterFunctional {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private WebTarget service;
	private RegisterPO registerPo;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		registerPo = new RegisterPO(service);
		TestUtils.deleteTestDb();
	}

	@Test
	public void shouldRegisterWithUsernameAndPassword() {
	    RegisterResourceReturnData result = registerPo.register("aaron@aaron.com", "passy");
		
		// Assert
		assertEquals("", result.getError());
		assertTrue("Should return true as 'successful' field", result.isSuccessful());
	}
	
	@Test
	public void shouldAdminBitSet() {
		// Arrange 
	    // Act
	    RegisterResourceReturnData result = registerPo.register("aaron", "aaron");
		LoginResourceReturnData login = new LoginPO(service).login("aaron", "aaron");

		// Assert
		assertTrue("Should register", result.isSuccessful());
		assertTrue("See admin bit", login.isAdmin()==true);
	}

	@Test
	public void shouldntSeeAdminBitSet() {
		// Arrange 
	    // Act
	    registerPo.register("aaron", "aaron");
	    RegisterResourceReturnData resultInput = registerPo.register("aaron1", "aaron1");
		LoginResourceReturnData login = new LoginPO(service).login("aaron1", "aaron1");

		// Assert
		assertTrue("Should register", resultInput.isSuccessful());
		assertFalse("Shouldn't see admin bit", login.isAdmin());
	}

	@Test
	public void shouldSeeErrorJsonOnExistingUsername() {
		// Arrange 

	    // Act 
	    RegisterResourceReturnData result = registerPo.register("aaron@aaron.com", "passy");
	    RegisterResourceReturnData result2 = registerPo.register("aaron@aaron.com", "passy");
		
		// Assert
		assertTrue("Should return true as 'successful' field", result.isSuccessful());
		assertEquals("Should see blank error JSON", "", result.getError());
		assertFalse("Should return false as 'successful' field", result2.isSuccessful());
		assertEquals("Should see error JSON", rb.getString(Strings.username_already_exists), result2.getError());
	}	
	
	@Test
	public void shouldSeeErrorJsonOnBlanksPassed() {
		// Arrange 
	    RegisterResourceReturnData result = registerPo.register("", "");
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnBlankUsername() {
		// Arrange 
	    RegisterResourceReturnData result = registerPo.register("", "passy");
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnBlankPassword() {
		// Arrange 
	    RegisterResourceReturnData result = registerPo.register("aaron@aaron.com", "");
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnNulls() {
		// Arrange 
	    RegisterResourceReturnData result = registerPo.register(null, null);

		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnNullUsername() {
		// Arrange 
	    RegisterResourceReturnData result = registerPo.register(null, "passy");
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}
	
	@Test
	public void shouldSeeErrorJsonOnNullPassword() {
		// Arrange 
	    RegisterResourceReturnData result = registerPo.register("username", null);
		
		// Assert
		assertEquals(rb.getString(Strings.user_pass_cannot_be_blank), result.getError());
		assertFalse("Should return false 'successful' field", result.isSuccessful());		
	}	

	
}

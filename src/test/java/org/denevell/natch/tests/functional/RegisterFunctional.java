package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceReturnData;
import org.denevell.natch.tests.functional.pageobjects.ListUsersPO;
import org.denevell.natch.tests.functional.pageobjects.LoginPO;
import org.denevell.natch.tests.functional.pageobjects.RegisterPO;
import org.denevell.natch.utils.Strings;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class RegisterFunctional {
	
    ResourceBundle rb = Strings.getMainResourceBundle();
	private WebTarget service;
	private RegisterPO registerPo;
	private LoginPO loginPo;
	private ListUsersPO listUsersPo;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		registerPo = new RegisterPO(service);
	    loginPo = new LoginPO(service);
	    listUsersPo = new ListUsersPO(service);
		TestUtils.deleteTestDb();
	}

	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Act
	    RegisterResourceReturnData result = 
	    		registerPo.register("aaron@aaron.com", "passy", "a@recovery.com");

		// Assert
		assertEquals("", result.getError());
	    LoginResourceReturnData adminLogin = loginPo.login("aaron@aaron.com", "passy");
	    assertEquals("Has recovery email set", "a@recovery.com", listUsersPo.findUser("aaron@aaron.com", adminLogin.getAuthKey()).getRecoveryEmail());
		assertTrue("Should return true as 'successful' field", result.isSuccessful());
	}

	@Test
	public void cantRegisterWithSameEmail() {
		// Act
	    RegisterResourceReturnData result1 = registerPo.register("aaron", "passy", "a@recovery.com");
	    RegisterResourceReturnData result2 = registerPo.register("xaron", "passy", "a@recovery.com");

		// Assert
		assertTrue("Should return true 'successful' field", result1.isSuccessful());
		assertEquals(rb.getString(Strings.email_already_exists), result2.getError());
		assertFalse("Should return false 'successful' field", result2.isSuccessful());
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
		try{
			registerPo.register(" ", " ");
		} catch(BadRequestException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		assertFalse("Excepted a 400", true);
	}
	
	@Test
	public void shouldSeeErrorJsonOnBlankUsername() {
		try{
			registerPo.register(" ", "passy");
		} catch(BadRequestException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		assertFalse("Excepted a 400", true);
	}
	
	@Test
	public void shouldSeeErrorJsonOnBlankPassword() {
		try{
			registerPo.register("aaron@aaron.com", " ");
		} catch(BadRequestException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		assertFalse("Excepted a 400", true);
	}
	
	@Test
	public void shouldSeeErrorJsonOnNulls() {
		try{
			registerPo.register(null, null);
		} catch(BadRequestException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		assertFalse("Excepted a 400", true);
	}
	
	@Test
	public void shouldSeeErrorJsonOnNullUsername() {
		try{
			registerPo.register(null, "passy");
		} catch(BadRequestException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		assertFalse("Excepted a 400", true);
	}
	
	@Test
	public void shouldSeeErrorJsonOnNullPassword() {
		try{
			registerPo.register("username", null);
		} catch(BadRequestException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}
		assertFalse("Excepted a 400", true);
	}	

	
}

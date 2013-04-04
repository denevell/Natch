package org.denevell.natch.tests.unit.login;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.junit.Before;
import org.junit.Test;

public class LoginAuthDataSingletonTests {
	
	private LoginAuthKeysSingleton authKeyGenerator;

	@Before
	public void setup() {
		authKeyGenerator = LoginAuthKeysSingleton.getInstance();
		authKeyGenerator.clearAllKeys();
	}
	
	@Test
	public void shouldGenerateAuthKeyForUsername() {
		// Arrange
		
		// Act
		String authKey = authKeyGenerator.generate("username");
		
		// Assert
		assertTrue(authKey.length()>5);
	}	
	
	@Test
	public void shouldReturnNullForNullUsernameGeneration() {
		// Arrange
		
		// Act
		String authKey = authKeyGenerator.generate(null);
		
		// Assert
		assertNull(authKey);
	}	
	
	@Test
	public void shouldReturnNullForBlankUsernameGeneration() {
		// Arrange
		
		// Act
		String authKey = authKeyGenerator.generate(" ");
		
		// Assert
		assertNull(authKey);
	}	
	
	@Test
	public void shouldGenerateDifferentAuthKeyForUsername() {
		// Arrange
		
		// Act
		String authKey = authKeyGenerator.generate("username");
		String authKey1 = authKeyGenerator.generate("username");
		
		// Assert
		assertFalse(authKey.equals(authKey1));
	}
	
	@Test
	public void shouldntLoginWithOldAuthKey() {
		// Arrange
		String authKey = authKeyGenerator.generate("username");
		authKeyGenerator.generate("username");
		
		// Act
		String username = authKeyGenerator.retrieveUsername(authKey);
		
		// Assert
		assertNull(username);
	}		
	
	@Test
	public void shouldRetrieveUsernameForKey() {
		// Arrange
		
		// Act
		String authKey = authKeyGenerator.generate("username");
		String retrievedUsername = authKeyGenerator.retrieveUsername(authKey);
		
		// Assert
		assertTrue("username".equals(retrievedUsername));
	}	
	
	@Test
	public void shouldReturnNullForNullKey() {
		// Arrange
		
		// Act
		String retrievedUsername = authKeyGenerator.retrieveUsername(null);
		
		// Assert
		assertNull(retrievedUsername);
	}	
	
	@Test
	public void shouldRetrieveUsernameForAuthKeyIfNewObject() {
		// Arrange
		
		// Act
		String authKey = authKeyGenerator.generate("username");
		authKeyGenerator = LoginAuthKeysSingleton.getInstance();
		String retrievedUsername = authKeyGenerator.retrieveUsername(authKey);
		
		// Assert
		assertTrue("username".equals(retrievedUsername));
	}	
	
	@Test
	public void shouldClearAllKeys() {
		// Arrange
		
		// Act
		String key = authKeyGenerator.generate("username");
		String retrievedUsername = authKeyGenerator.retrieveUsername(key);
		authKeyGenerator.clearAllKeys();
		String retrievedUsername1 = authKeyGenerator.retrieveUsername(key);
		
		// Assert
		assertNotNull(retrievedUsername);
		assertNull(retrievedUsername1);
	}	
	
	@Test
	public void shouldRemoveAuthKey() {
		// Arrange
		String authKey = authKeyGenerator.generate("username");
		
		// Act
		String retrievedUsername = authKeyGenerator.retrieveUsername(authKey);
		authKeyGenerator.remove(authKey);
		String retrievedUsername2 = authKeyGenerator.retrieveUsername(authKey);
		
		// Assert
		assertNotNull(retrievedUsername);
		assertNull(retrievedUsername2);
	}		

}

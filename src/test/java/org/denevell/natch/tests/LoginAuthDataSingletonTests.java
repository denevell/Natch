package org.denevell.natch.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.denevell.natch.login.LoginAuthDataSingleton;
import org.junit.Before;
import org.junit.Test;

public class LoginAuthDataSingletonTests {
	
	private LoginAuthDataSingleton authKeyGenerator;

	@Before
	public void setup() {
		authKeyGenerator = LoginAuthDataSingleton.getInstance();
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
		String retrievedAuthKey = authKeyGenerator.retrieve("username");
		
		// Assert
		assertFalse(authKey.equals(authKey1));
		assertFalse(authKey.equals(retrievedAuthKey));
		assertTrue(authKey1.equals(retrievedAuthKey));
	}	
	
	@Test
	public void shouldRetrieveAuthKeyForUsername() {
		// Arrange
		
		// Act
		String authKey = authKeyGenerator.generate("username");
		String retrievedAuthKey = authKeyGenerator.retrieve("username");
		
		// Assert
		assertTrue(authKey.equals(retrievedAuthKey));
	}	
	
	@Test
	public void shouldReturnNullForNullRetrieve() {
		// Arrange
		
		// Act
		String retrievedAuthKey = authKeyGenerator.retrieve(null);
		
		// Assert
		assertNull(retrievedAuthKey);
	}	
	
	@Test
	public void shouldRetrieveAuthKeyForUsernameInNewObject() {
		// Arrange
		
		// Act
		String authKey = authKeyGenerator.generate("username");
		authKeyGenerator = LoginAuthDataSingleton.getInstance();
		String retrievedAuthKey = authKeyGenerator.retrieve("username");
		
		// Assert
		assertTrue(authKey.equals(retrievedAuthKey));
	}	
	
	@Test
	public void shouldClearAllKeys() {
		// Arrange
		
		// Act
		authKeyGenerator.generate("username");
		String retrievedAuthKey = authKeyGenerator.retrieve("username");
		authKeyGenerator.clearAllKeys();
		String retrievedAuthKey1 = authKeyGenerator.retrieve("username");
		
		// Assert
		assertNotNull(retrievedAuthKey);
		assertNull(retrievedAuthKey1);
	}	

}

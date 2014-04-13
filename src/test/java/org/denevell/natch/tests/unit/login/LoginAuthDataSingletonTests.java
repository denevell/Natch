package org.denevell.natch.tests.unit.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.model.entities.UserEntity;
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
		UserEntity userEntity = new UserEntity("username", "password");
		
		// Act
		String authKey = authKeyGenerator.generate(userEntity);
		
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
	public void shouldGenerateDifferentAuthKeyForUsername() {
		// Arrange
		UserEntity userEntity = new UserEntity("username", "password");
		
		// Act
		String authKey = authKeyGenerator.generate(userEntity);
		String authKey1 = authKeyGenerator.generate(userEntity);
		
		// Assert
		assertFalse(authKey.equals(authKey1));
	}
	
	@Test
	public void shouldntLoginWithOldAuthKey() {
		// Arrange
		UserEntity userEntity = new UserEntity();
		String authKey = authKeyGenerator.generate(userEntity);
		authKeyGenerator.generate(userEntity);
		
		// Act
		UserEntity username = authKeyGenerator.retrieveUserEntity(authKey);
		
		// Assert
		assertNull(username);
	}		
	
	@Test
	public void shouldRetrieveUsernameForKey() {
		// Arrange
		UserEntity userEntity = new UserEntity("username", "password");
		
		// Act
		String authKey = authKeyGenerator.generate(userEntity);
		UserEntity retrievedUsername = authKeyGenerator.retrieveUserEntity(authKey);
		
		// Assert
		assertTrue(userEntity == retrievedUsername);
	}	
	
	@Test
	public void shouldReturnNullForNullKey() {
		// Arrange
		
		// Act
		UserEntity retrievedUsername = authKeyGenerator.retrieveUserEntity(null);
		
		// Assert
		assertNull(retrievedUsername);
	}	
	
	@Test
	public void shouldRetrieveUsernameForAuthKeyIfNewObject() {
		// Arrange
		UserEntity userEntity = new UserEntity("username", "password");
		
		// Act
		String authKey = authKeyGenerator.generate(userEntity);
		authKeyGenerator = LoginAuthKeysSingleton.getInstance();
		UserEntity retrievedUsername = authKeyGenerator.retrieveUserEntity(authKey);
		
		// Assert
		assertEquals(userEntity, retrievedUsername);
	}	
	
	@Test
	public void shouldClearAllKeys() {
		// Arrange
		UserEntity userEntity = new UserEntity("username", "password");
		
		// Act
		String key = authKeyGenerator.generate(userEntity);
		UserEntity retrievedUsername = authKeyGenerator.retrieveUserEntity(key);
		authKeyGenerator.clearAllKeys();
		UserEntity retrievedUsername1 = authKeyGenerator.retrieveUserEntity(key);
		
		// Assert
		assertNotNull(retrievedUsername);
		assertNull(retrievedUsername1);
	}	
	
	@Test
	public void shouldRemoveAuthKey() {
		// Arrange
		UserEntity userEntity = new UserEntity("username", "password");
		String authKey = authKeyGenerator.generate(userEntity);
		
		// Act
		UserEntity retrievedUsername = authKeyGenerator.retrieveUserEntity(authKey);
		authKeyGenerator.remove(authKey);
		UserEntity retrievedUsername2 = authKeyGenerator.retrieveUserEntity(authKey);
		
		// Assert
		assertNotNull(retrievedUsername);
		assertNull(retrievedUsername2);
	}		

}

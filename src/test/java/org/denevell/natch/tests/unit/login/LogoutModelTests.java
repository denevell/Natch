package org.denevell.natch.tests.unit.login;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.users.logout.LogoutModel;
import org.junit.Before;
import org.junit.Test;

public class LogoutModelTests {
	
	private LogoutModel loginModel;
	private LoginAuthKeysSingleton authKeyGenerator;
	private EntityTransaction trans;
	private EntityManager entityManager;

	@Before
	public void setup() {
		trans = mock(EntityTransaction.class);
		entityManager = mock(EntityManager.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		authKeyGenerator = mock(LoginAuthKeysSingleton.class);
		loginModel = new LogoutModel(authKeyGenerator);		
	}
	
	@Test
	public void shouldLogoutWhenAuthKeyExists() {
		// Arrange
		when(authKeyGenerator.retrieveUserEntity("authKey")).thenReturn(null);
		
		// Act
		boolean result = loginModel.logout("authKey");
		
		// Assert
		assertTrue(result);
	}
	
	@Test
	public void shouldntLogoutWhenAuthKeyStillExists() {
		// Arrange
		when(authKeyGenerator.retrieveUserEntity("otherAuthKey")).thenReturn(new UserEntity());
		
		// Act
		boolean result = loginModel.logout("otherAuthKey");
		
		// Assert
		assertFalse(result);
	}
	
	@Test
	public void shouldntLogoutWhenAuthKeyIsNull() {
		// Arrange
		
		// Act
		boolean result = loginModel.logout(null);
		
		// Assert
		assertFalse(result);
	}
	
	@Test
	public void shouldntLogoutWhenAuthKeyIsBlank() {
		// Arrange
		
		// Act
		boolean result = loginModel.logout(" ");
		
		// Assert
		assertFalse(result);
	}
	
}
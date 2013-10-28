package org.denevell.natch.tests.unit.register;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.users.logout.LogoutModel;
import org.denevell.natch.serv.users.register.RegisterModel;
import org.denevell.natch.utils.PasswordSaltUtils;
import org.junit.Before;
import org.junit.Test;

public class RegisterModelTests {
	
	private EntityTransaction trans;
	private EntityManager entityManager;
	private PasswordSaltUtils salter;
	private LoginAuthKeysSingleton authManager;

	@Before
	public void setup() {
		trans = mock(EntityTransaction.class);
		salter = mock(PasswordSaltUtils.class);
		entityManager = mock(EntityManager.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		authManager = mock(LoginAuthKeysSingleton.class);
	}
	
	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		RegisterModel um = spy(new RegisterModel(authManager, entityManager, salter));
		doReturn(false).when(um).doesUsernameExist("user", entityManager);
		doReturn(false).when(um).isFirstUser();
		
		// Act
		String result = um.addUserToSystem("user", "pass");
		
		// Assert
		assertEquals("Successfully register", RegisterModel.REGISTERED, result);
	}
	
	@Test
	public void shouldntRegisterWithBlankUsername() {
		// ArrangeaddUserToSystem
		RegisterModel um = new RegisterModel(authManager, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("", "asd");
		
		// Assert
		assertEquals(RegisterModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullUsername() {
		// Arrange
		RegisterModel um = new RegisterModel(authManager, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem(null, "asd");
		
		// Assert
		assertEquals(LogoutModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithBlankPassword() {
		// Arrange
		RegisterModel um = new RegisterModel(authManager, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("dsfd", "");
		
		// Assert
		assertEquals(LogoutModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullPassword() {
		// Arrange
		RegisterModel um = new RegisterModel(authManager, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("dsfd", null);
		
		// Assert
		assertEquals(LogoutModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithEntityMangerException() { 
		// Arrange
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		RegisterModel um = new RegisterModel(authManager, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("dsfd", "dsfsdf");
		
		// Assert
		assertEquals(LogoutModel.UNKNOWN_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithDuplicateUsername() { 
		// Arrange
		RegisterModel um = spy(new RegisterModel(authManager, entityManager, salter));
		doReturn(true).when(um).doesUsernameExist("username", entityManager);
		doReturn(false).when(um).isFirstUser();
		
		// ActtoBeReturned
		String result = um.addUserToSystem("username", "dsfsdf");
		
		// Assert
		assertEquals(RegisterModel.DUPLICATE_USERNAME, result);
	}	
	
	@Test
	public void shouldShowFirstUser() { 
		// Arrange
		RegisterModel um = new RegisterModel(authManager, entityManager, salter);
		Query q = mock(Query.class);
		when(q.getSingleResult()).thenReturn(0);
		when(entityManager.createNamedQuery(UserEntity.NAMED_QUERY_COUNT)).thenReturn(q);
		
		// Act
		boolean result = um.isFirstUser();
		
		// Assert
		assertTrue("Is first user", result);
	}	
	
	@Test
	public void shouldntShowFirstUser() { 
		// Arrange
		RegisterModel um = new RegisterModel(authManager, entityManager, salter);
		Query q = mock(Query.class);
		when(q.getSingleResult()).thenReturn(1);
		when(entityManager.createNamedQuery(UserEntity.NAMED_QUERY_COUNT)).thenReturn(q);
		
		// Act
		boolean result = um.isFirstUser();
		
		// Assert
		assertFalse("Is first user", result);
	}		
	

}
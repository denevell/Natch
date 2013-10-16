package org.denevell.natch.tests.unit.register;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.serv.users.logout.LogoutModel;
import org.denevell.natch.serv.users.register.RegisterModel;
import org.denevell.natch.utils.PasswordSaltUtils;
import org.junit.Before;
import org.junit.Test;

public class RegisterModelTests {
	
	private UserEntityQueries queries;
	private EntityTransaction trans;
	private EntityManager entityManager;
	private EntityManagerFactory factory;
	private PasswordSaltUtils salter;
	private LoginAuthKeysSingleton authManager;

	@Before
	public void setup() {
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		salter = mock(PasswordSaltUtils.class);
		entityManager = mock(EntityManager.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		authManager = mock(LoginAuthKeysSingleton.class);
		queries = mock(UserEntityQueries.class);
	}
	
	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		RegisterModel um = new RegisterModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("user", "pass");
		
		// Assert
		assertEquals("Successfully register", RegisterModel.REGISTERED, result);
	}
	
	@Test
	public void shouldntRegisterWithBlankUsername() {
		// Arrange
		RegisterModel um = new RegisterModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("", "asd");
		
		// Assert
		assertEquals(RegisterModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullUsername() {
		// Arrange
		RegisterModel um = new RegisterModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem(null, "asd");
		
		// Assert
		assertEquals(LogoutModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithBlankPassword() {
		// Arrange
		RegisterModel um = new RegisterModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("dsfd", "");
		
		// Assert
		assertEquals(LogoutModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullPassword() {
		// Arrange
		RegisterModel um = new RegisterModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("dsfd", null);
		
		// Assert
		assertEquals(LogoutModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithEntityMangerException() { 
		// Arrange
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		RegisterModel um = new RegisterModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("dsfd", "dsfsdf");
		
		// Assert
		assertEquals(LogoutModel.UNKNOWN_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithDuplicateUsername() { 
		// Arrange
		when(queries.doesUsernameExist("username", entityManager)).thenReturn(true);
		RegisterModel um = new RegisterModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("username", "dsfsdf");
		
		// Assert
		assertEquals(RegisterModel.DUPLICATE_USERNAME, result);
	}	

}
package org.denevell.natch.tests.unit.register;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.serv.users.UsersModel;
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
		UsersModel um = new UsersModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("user", "pass");
		
		// Assert
		assertEquals("Successfully register", UsersModel.REGISTERED, result);
	}
	
	@Test
	public void shouldntRegisterWithBlankUsername() {
		// Arrange
		UsersModel um = new UsersModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("", "asd");
		
		// Assert
		assertEquals(UsersModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullUsername() {
		// Arrange
		UsersModel um = new UsersModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem(null, "asd");
		
		// Assert
		assertEquals(UsersModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithBlankPassword() {
		// Arrange
		UsersModel um = new UsersModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("dsfd", "");
		
		// Assert
		assertEquals(UsersModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullPassword() {
		// Arrange
		UsersModel um = new UsersModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("dsfd", null);
		
		// Assert
		assertEquals(UsersModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithEntityMangerException() { 
		// Arrange
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		UsersModel um = new UsersModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("dsfd", "dsfsdf");
		
		// Assert
		assertEquals(UsersModel.UNKNOWN_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithDuplicateUsername() { 
		// Arrange
		when(queries.doesUsernameExist("username")).thenReturn(true);
		UsersModel um = new UsersModel(queries, authManager, factory, entityManager, salter);
		
		// Act
		String result = um.addUserToSystem("username", "dsfsdf");
		
		// Assert
		assertEquals(UsersModel.DUPLICATE_USERNAME, result);
	}	

}
package org.denevell.natch.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.serv.register.RegisterModel;
import org.denevell.natch.serv.register.RegisterModel.RegisterResult;
import org.denevell.natch.utils.PasswordSaltUtils;
import org.junit.Before;
import org.junit.Test;

public class RegisterModelTests {
	
	private UserEntityQueries queries;
	private EntityTransaction trans;
	private EntityManager entityManager;
	private EntityManagerFactory factory;
	private PasswordSaltUtils salter;

	@Before
	public void setup() {
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		salter = mock(PasswordSaltUtils.class);
		entityManager = mock(EntityManager.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		queries = mock(UserEntityQueries.class);
	}
	
	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		RegisterModel um = new RegisterModel(entityManager, factory, queries, salter);
		
		// Act
		RegisterResult result = um.addUserToSystem("user", "pass");
		
		// Assert
		assertEquals("Successfully register", RegisterResult.REGISTERED, result);
	}
	
	@Test
	public void shouldntRegisterWithBlankUsername() {
		// Arrange
		RegisterModel um = new RegisterModel(entityManager, factory, queries, salter);
		
		// Act
		RegisterResult result = um.addUserToSystem("", "asd");
		
		// Assert
		assertEquals(RegisterResult.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullUsername() {
		// Arrange
		RegisterModel um = new RegisterModel(entityManager, factory, queries, salter);
		
		// Act
		RegisterResult result = um.addUserToSystem(null, "asd");
		
		// Assert
		assertEquals(RegisterResult.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithBlankPassword() {
		// Arrange
		RegisterModel um = new RegisterModel(entityManager, factory, queries, salter);
		
		// Act
		RegisterResult result = um.addUserToSystem("dsfd", "");
		
		// Assert
		assertEquals(RegisterResult.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullPassword() {
		// Arrange
		RegisterModel um = new RegisterModel(entityManager, factory, queries, salter);
		
		// Act
		RegisterResult result = um.addUserToSystem("dsfd", null);
		
		// Assert
		assertEquals(RegisterResult.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithEntityMangerException() { 
		// Arrange
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		RegisterModel um = new RegisterModel(entityManager, factory, queries, salter);
		
		// Act
		RegisterResult result = um.addUserToSystem("dsfd", "dsfsdf");
		
		// Assert
		assertEquals(RegisterResult.UNKNOWN_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithDuplicateUsername() { 
		// Arrange
		when(queries.doesUsernameExist("username")).thenReturn(true);
		RegisterModel um = new RegisterModel(entityManager, factory, queries, salter);
		
		// Act
		RegisterResult result = um.addUserToSystem("username", "dsfsdf");
		
		// Assert
		assertEquals(RegisterResult.DUPLICATE_USERNAME, result);
	}	

}
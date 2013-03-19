package org.denevell.natch.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.models.UserModel;
import org.junit.Before;
import org.junit.Test;

public class RegisterModelTests {
	
	private UserEntityQueries queries;
	private EntityTransaction trans;
	private EntityManager entityManager;
	private EntityManagerFactory factory;

	@Before
	public void setup() {
		factory = mock(EntityManagerFactory.class);
		entityManager = mock(EntityManager.class);
		trans = mock(EntityTransaction.class);
		queries = mock(UserEntityQueries.class);
	}
	
	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		when(entityManager.getTransaction()).thenReturn(trans);
		UserModel um = new UserModel(entityManager, factory, queries);
		
		// Act
		boolean result = um.addUserToSystem("user", "pass");
		
		// Assert
		assertTrue("Successfully register", result);
	}
	
	@Test
	public void shouldntRegisterWithBlankUsername() {
		// Arrange
		when(entityManager.getTransaction()).thenReturn(trans);
		UserModel um = new UserModel(entityManager, factory, queries);
		
		// Act
		boolean result = um.addUserToSystem("", "asd");
		
		// Assert
		assertFalse("Successfully register", result);
	}
	
	@Test
	public void shouldntRegisterWithNullUsername() {
		// Arrange
		when(entityManager.getTransaction()).thenReturn(trans);
		UserModel um = new UserModel(entityManager, factory, queries);
		
		// Act
		boolean result = um.addUserToSystem(null, "asd");
		
		// Assert
		assertFalse("Successfully register", result);
	}
	
	@Test
	public void shouldntRegisterWithBlankPassword() {
		// Arrange
		when(entityManager.getTransaction()).thenReturn(trans);
		UserModel um = new UserModel(entityManager, factory, queries);
		
		// Act
		boolean result = um.addUserToSystem("dsfd", "");
		
		// Assert
		assertFalse("Successfully register", result);
	}
	
	@Test
	public void shouldntRegisterWithNullPassword() {
		// Arrange
		when(entityManager.getTransaction()).thenReturn(trans);
		UserModel um = new UserModel(entityManager, factory, queries);
		
		// Act
		boolean result = um.addUserToSystem("dsfd", null);
		
		// Assert
		assertFalse("Successfully register", result);
	}
	
	@Test
	public void shouldntRegisterWithEntityMangerException() { 
		// Arrange
		when(entityManager.getTransaction()).thenThrow(new RuntimeException());
		UserModel um = new UserModel(entityManager, factory, queries);
		
		// Act
		boolean result = um.addUserToSystem("dsfd", "dsfsdf");
		
		// Assert
		assertFalse("Successfully register", result);
	}
	
	@Test
	public void shouldntRegisterWithDuplicateUsername() { 
		// Arrange
		when(queries.doesUsernameExist("username")).thenReturn(true);
		UserModel um = new UserModel(entityManager, factory, queries);
		
		// Act
		boolean result = um.addUserToSystem("dsfd", "dsfsdf");
		
		// Assert
		assertFalse("Unsuccessfully register", result);
	}	

}
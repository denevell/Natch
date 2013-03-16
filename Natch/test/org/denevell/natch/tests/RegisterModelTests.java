package org.denevell.natch.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.models.UserModel;
import org.junit.Test;

public class RegisterModelTests {
	
	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		EntityManagerFactory factory = mock(EntityManagerFactory.class);
		EntityManager entityManager = mock(EntityManager.class);
		EntityTransaction trans = mock(EntityTransaction.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		UserModel um = new UserModel(entityManager, factory);
		
		// Act
		boolean result = um.addUserToSystem("user", "pass");
		
		// Assert
		assertTrue("Successfully register", result);
	}
	
	@Test
	public void shouldntRegisterWithBlanks() {
		// Arrange
		EntityManagerFactory factory = mock(EntityManagerFactory.class);
		EntityManager entityManager = mock(EntityManager.class);
		EntityTransaction trans = mock(EntityTransaction.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		UserModel um = new UserModel(entityManager, factory);
		
		// Act
		boolean result = um.addUserToSystem("", "");
		
		// Assert
		assertFalse("Successfully register", result);
	}

}
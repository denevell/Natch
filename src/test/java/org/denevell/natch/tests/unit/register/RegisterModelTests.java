package org.denevell.natch.tests.unit.register;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.Query;

import org.denevell.jrappy.Jrappy;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Ignored some of the test after the large refactor
 * @author user
 *
 */
public class RegisterModelTests {
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private Jrappy entityManager;

	@Before
	public void setup() {
		entityManager = mock(Jrappy.class);
	}
	
	@Ignore
	@Test
	public void shouldRegisterWithUsernameAndPassword() {
		// Arrange
		//RegisterModel um = spy(new RegisterModel(entityManager));
		//doReturn(false).when(um).doesUsernameExist("user", entityManager);
		//doReturn(false).when(um).isFirstUser();
		
		// Act
		//String result = um.addUserToSystem("user", "pass");
		
		// Assert
		//assertEquals("Successfully register", RegisterModel.REGISTERED, result);
	}
	
	@Ignore
	@Test
	public void shouldntRegisterWithBlankUsername() {
		// ArrangeaddUserToSystem
		//RegisterModel um = spy(new RegisterModel(entityManager));
		
		// Act
		//String result = um.addUserToSystem("", "asd");
		
		// Assert
		//assertEquals(RegisterModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullUsername() {
		// Arrange
		//RegisterModel um = spy(new RegisterModel(entityManager));
		
		// Act
		//String result = um.addUserToSystem(null, "asd");
		
		// Assert
		//assertEquals(LogoutModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithBlankPassword() {
		// Arrange
		//RegisterModel um = spy(new RegisterModel(entityManager));
		
		// Act
		//String result = um.addUserToSystem("dsfd", "");
		
		// Assert
		//assertEquals(LogoutModel.USER_INPUT_ERROR, result);
	}
	
	@Test
	public void shouldntRegisterWithNullPassword() {
		// Arrange
		//RegisterModel um = spy(new RegisterModel(entityManager));
		
		// Act
		//String result = um.addUserToSystem("dsfd", null);
		
		// Assert
		//assertEquals(LogoutModel.USER_INPUT_ERROR, result);
	}
	
	@Ignore
	@Test
	public void shouldntRegisterWithEntityMangerException() { 
		// Arrange
		//RegisterModel um = spy(new RegisterModel(entityManager));
		
		// Act
		//String result = um.addUserToSystem("dsfd", "dsfsdf");
		
		// Assert
		//assertEquals(LogoutModel.UNKNOWN_ERROR, result);
	}
	
	@Ignore
	@Test
	public void shouldntRegisterWithDuplicateUsername() { 
		// Arrange
		//RegisterModel um = spy(new RegisterModel(entityManager));
		//doReturn(true).when(um).doesUsernameExist("username", entityManager);
		//doReturn(false).when(um).isFirstUser();
		
		// ActtoBeReturned
		//String result = um.addUserToSystem("username", "dsfsdf");
		
		// Assert
		//assertEquals(RegisterModel.DUPLICATE_USERNAME, result);
	}	
	
	@Ignore
	@Test
	public void shouldShowFirstUser() { 
		// Arrange
		//RegisterModel um = spy(new RegisterModel(entityManager));
		Query q = mock(Query.class);
		when(q.getSingleResult()).thenReturn(0);
		//when(entityManager.createNamedQuery(UserEntity.NAMED_QUERY_COUNT)).thenReturn(q);
		
		// Act
		//boolean result = um.isFirstUser();
		
		// Assert
		//assertTrue("Is first user", result);
	}	
	
	@Ignore
	@Test
	public void shouldntShowFirstUser() { 
		// Arrange
        //RegisterModel um = spy(new RegisterModel(entityManager));
		Query q = mock(Query.class);
		when(q.getSingleResult()).thenReturn(1);
		//when(entityManager.createNamedQuery(UserEntity.NAMED_QUERY_COUNT)).thenReturn(q);
		
		// Act
		//boolean result = um.isFirstUser();
		
		// Assert
		//assertFalse("Is first user", result);
	}		
	

}
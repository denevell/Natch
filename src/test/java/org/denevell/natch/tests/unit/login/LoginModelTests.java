package org.denevell.natch.tests.unit.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.serv.users.login.LoginModel;
import org.denevell.natch.serv.users.login.LoginModel.LoginResult;
import org.denevell.natch.serv.users.logout.LogoutModel;
import org.junit.Before;
import org.junit.Test;

public class LoginModelTests {
	
	private UserEntityQueries queries;
	private LoginModel loginModel;
	private LoginAuthKeysSingleton authKeyGenerator;
	private EntityManagerFactory factory;
	private EntityTransaction trans;
	private EntityManager entityManager;

	@Before
	public void setup() {		
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		entityManager = mock(EntityManager.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		queries = mock(UserEntityQueries.class);
		authKeyGenerator = mock(LoginAuthKeysSingleton.class);
		loginModel = new LoginModel(queries, authKeyGenerator, factory, entityManager);
	}
	
	@Test
	public void shouldLoginWithUsernameAndPassword() {
		// Arrange
		when(queries.areCredentialsCorrect("username", "password", entityManager)).thenReturn(new UserEntity());
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", LogoutModel.LOGGED_IN, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithIncorrectUsernameAndPassword() {
		// Arrange
		when(queries.areCredentialsCorrect("username", "password", entityManager)).thenReturn(null);
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", LogoutModel.CREDENTIALS_INCORRECT, result.getResult());
	}
	
	@Test
	public void shouldReturnLoginAuthKey() {
		// Arrange
		UserEntity userEntity = new UserEntity();
		when(queries.areCredentialsCorrect("username", "password", entityManager)).thenReturn(userEntity);
		when(authKeyGenerator.generate(userEntity)).thenReturn("authKey123");
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertEquals("authKey123", result.getAuthKey());
	}
	
	@Test
	public void shouldntReturnLoginAuthKeyOnBadCredentials() {
		// Arrange
		when(queries.areCredentialsCorrect("username", "password", entityManager)).thenReturn(null);
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertTrue("Blank auth key on incorrect credentials", result.getAuthKey().length()==0);
	}

	
}
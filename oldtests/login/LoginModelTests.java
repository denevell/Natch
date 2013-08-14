package org.denevell.natch.tests.unit.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.serv.users.UsersModel;
import org.denevell.natch.serv.users.UsersModel.LoginResult;
import org.denevell.natch.utils.PasswordSaltUtils;
import org.junit.Before;
import org.junit.Test;

public class LoginModelTests {
	
	private UserEntityQueries queries;
	private UsersModel loginModel;
	private LoginAuthKeysSingleton authKeyGenerator;
	private EntityManagerFactory factory;
	private EntityTransaction trans;
	private PasswordSaltUtils salter;
	private EntityManager entityManager;

	@Before
	public void setup() {		
		factory = mock(EntityManagerFactory.class);
		trans = mock(EntityTransaction.class);
		salter = mock(PasswordSaltUtils.class);
		entityManager = mock(EntityManager.class);
		when(entityManager.getTransaction()).thenReturn(trans);
		queries = mock(UserEntityQueries.class);
		authKeyGenerator = mock(LoginAuthKeysSingleton.class);
		loginModel = new UsersModel(queries, authKeyGenerator, factory, entityManager, salter);
	}
	
	@Test
	public void shouldLoginWithUsernameAndPassword() {
		// Arrange
		when(queries.areCredentialsCorrect("username", "password", entityManager)).thenReturn(new UserEntity());
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", UsersModel.LOGGED_IN, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithIncorrectUsernameAndPassword() {
		// Arrange
		when(queries.areCredentialsCorrect("username", "password", entityManager)).thenReturn(null);
		
		// Act
		LoginResult result = loginModel.login("username", "password");
		
		// Assert
		assertEquals("Successfully register", UsersModel.CREDENTIALS_INCORRECT, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithBlanks() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login(" ", " ");
		
		// Assert
		assertEquals("Fail to register", UsersModel.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithBlankUsername() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login(" ", "password");
		
		// Assert
		assertEquals("Fail to register", UsersModel.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithBlankPassword() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login("username", " ");
		
		// Assert
		assertEquals("Fail to register", UsersModel.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithNulls() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login(null, null);
		
		// Assert
		assertEquals("Fail to register", UsersModel.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithNullUsername() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login(null, "password");
		
		// Assert
		assertEquals("Fail to register", UsersModel.USER_INPUT_ERROR, result.getResult());
	}
	
	@Test
	public void shouldntLoginWithNullPassword() {
		// Arrange
		
		// Act
		LoginResult result = loginModel.login("username", null);
		
		// Assert
		assertEquals("Fail to register", UsersModel.USER_INPUT_ERROR, result.getResult());
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
	
	@Test
	public void shouldBeLoggedInWithCorrectAuthKey() {
		// Arrange
		String authKey = "auth123";
		UserEntity userEntity = new UserEntity();
		when(authKeyGenerator.retrieveUserEntity("auth123")).thenReturn(userEntity);
		
		// Act
		UserEntity username = loginModel.loggedInAs(authKey);
		
		// Assert
		assertEquals(userEntity, username);
	}
	
	@Test
	public void shouldntBeLoggedInWithIncorrectAuthKey() {
		// Arrange
		when(authKeyGenerator.retrieveUserEntity("auth123")).thenReturn(new UserEntity());
		
		// Act
		UserEntity username = loginModel.loggedInAs("badAuth123");
		
		// Assert
		assertNull(username);
	}	
	
	@Test
	public void shouldntBeLoggedInWithNull() {
		// Act
		UserEntity username = loginModel.loggedInAs(null);
		
		// Assert
		assertNull(username);
	}	
}
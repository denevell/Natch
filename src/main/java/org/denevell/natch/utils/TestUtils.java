package org.denevell.natch.utils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.denevell.natch.db.entities.PersistenceInfo;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.PushEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class TestUtils {
	
	private static final String NATCH_FUNCTIONAL = "/rest/";

	private static String getHost() {
		return "http://localhost:8085";
	}

	public static void deleteTestDb() throws Exception {
		EntityManager mEntityManager = null;
		EntityManagerFactory fact = null;
		try {
			fact = Persistence.createEntityManagerFactory(PersistenceInfo.TestEntityManagerFactoryName);
			mEntityManager = fact.createEntityManager();
			EntityTransaction trans = mEntityManager.getTransaction();
			trans.begin();
			List<ThreadEntity> resultT = mEntityManager.createQuery("select a from ThreadEntity a", ThreadEntity.class).getResultList();
			for (ThreadEntity postEntity : resultT) {
				mEntityManager.remove(postEntity);
			}
			List<PostEntity> result = mEntityManager.createQuery("select a from PostEntity a", PostEntity.class).getResultList();
			for (PostEntity postEntity : result) {
				mEntityManager.remove(postEntity);
			}
			List<UserEntity> resultU = mEntityManager.createQuery("select a from UserEntity a", UserEntity.class).getResultList();
			for (UserEntity postEntity : resultU) {
				mEntityManager.remove(postEntity);
			}
			List<PushEntity> resultP = mEntityManager.createQuery("select a from PushEntity a", PushEntity.class).getResultList();
			for (PushEntity postEntity : resultP) {
				mEntityManager.remove(postEntity);
			}
			trans.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			EntityUtils.closeEntityConnection(mEntityManager);
		}
	}	

	private static Client getClient() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		return client;
	}

	public static WebTarget getRESTClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    Client client = getClient();
	    return client.target(baseUrl);		
	}

	public static WebTarget getRESTRegisterClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    Client client = getClient();
	    return client.target(baseUrl).path("rest").path("user");		
	}

	public static WebTarget getLoginClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    Client client = getClient();
	    return client.target(baseUrl).path("rest").path("user").path("login");
	}

	public static WebTarget getRegisterClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    Client client = getClient();
	    return client.target(baseUrl).path("rest").path("user");		
	}

	public static WebTarget getDeletePostClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    Client client = getClient();
		return client.target(baseUrl).path("rest").path("post").path("del");		
	}

	public static WebTarget getEditPostClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    Client client = getClient();
		return client.target(baseUrl).path("rest").path("post").path("edit");		
	}

	public static WebTarget getListPostsClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    Client client = getClient();
		return client.target(baseUrl).path("rest").path("post");		
	}

	public static WebTarget getListPostThreadsPostClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    Client client = getClient();
		return client.target(baseUrl).path("rest").path("threads");		
	}

//	public static void addUser(String username, String password) {
//	    RegisterResourceInput registerInput = new RegisterResourceInput(username, password);
//		TestUtils.getRegisterClient()
//		.type(MediaType.APPLICATION_JSON)
//	    	.put(RegisterResourceReturnData.class, registerInput);		
//	}
//	
//	public static String loginUser(String username, String password) {
//	    LoginResourceInput input = new LoginResourceInput(username, password);
//		LoginResourceReturnData ret = TestUtils.getLoginClient()
//		.type(MediaType.APPLICATION_JSON)
//	    .post(LoginResourceReturnData.class, input);		
//		return ret.getAuthKey();
//	}
	

}

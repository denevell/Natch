package org.denevell.natch.utils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.db.entities.PersistenceInfo;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.users.LoginResourceInput;
import org.denevell.natch.io.users.LoginResourceReturnData;
import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.io.users.RegisterResourceReturnData;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class TestUtils {
	
	private static final String NATCH_FUNCTIONAL = "/Natch-REST-ForAutomatedTests/";

	private static String getHost() {
		return "http://localhost:8080";
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
			trans.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			EntityUtils.closeEntityConnection(mEntityManager);
		}
	}	

	public static WebResource getRESTClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl);		
	}

	public static WebResource getRESTRegisterClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl).path("rest").path("user");		
	}

	public static WebResource getLoginClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl).path("rest").path("user").path("login");
	}

	public static WebResource getRegisterClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl).path("rest").path("user");		
	}

	public static WebResource getAddPostClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("add");		
	}

	public static WebResource getAddThreadClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("addthread");		
	}

	public static WebResource getDeletePostClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("del");		
	}

	public static WebResource getEditPostClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("edit");		
	}

	public static WebResource getListPostsClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post");		
	}

	public static WebResource getListPostThreadsPostClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("threads");		
	}

	public static void addUser(String username, String password) {
	    RegisterResourceInput registerInput = new RegisterResourceInput(username, password);
		TestUtils.getRegisterClient()
		.type(MediaType.APPLICATION_JSON)
	    	.put(RegisterResourceReturnData.class, registerInput);		
	}
	
	public static String loginUser(String username, String password) {
	    LoginResourceInput input = new LoginResourceInput(username, password);
		LoginResourceReturnData ret = TestUtils.getLoginClient()
		.type(MediaType.APPLICATION_JSON)
	    .post(LoginResourceReturnData.class, input);		
		return ret.getAuthKey();
	}
	

}

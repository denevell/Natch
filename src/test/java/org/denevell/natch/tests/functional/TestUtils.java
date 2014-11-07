package org.denevell.natch.tests.functional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.validation.ValidationError;

public class TestUtils {
	
	//private static final String NATCH_FUNCTIONAL = "/Natch-REST-ForAutomatedTests/";

	public static final String URL_USER_SERVICE = "http://localhost:8084/cus/";
	public static final String URL_REST_SERVICE = "http://localhost:8084/";
	private static final String NATCH_FUNCTIONAL = "/";
	private static String getHost() {
		return "http://localhost:8084";
	}

	public static void deleteTestDb() throws Exception {
        Properties connectionProps = new Properties();
        connectionProps.put("user", "denevell");
        connectionProps.put("password", "user");

        @SuppressWarnings("unused")
		Class<?> c = Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://" +
                		"localhost" +
                        ":5432" + "/testnatch",
                connectionProps);

        Statement statement = conn.createStatement();
        statement.execute("delete from users_loggedin");
        statement.execute("delete from thread_posts");
        statement.execute("delete from post_tags");
        statement.execute("delete from ThreadEntity");
        statement.execute("delete from PostEntity");
        statement.execute("delete from UserEntity");
        statement.execute("delete from PushIds");
	}	

	public static WebTarget getRESTClient() {
		String baseUrl = getHost() + NATCH_FUNCTIONAL;
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
	    return client.target(baseUrl);		
	}
	
	public static List<ValidationError> getValidationErrors(Response response) {
		return response.readEntity(new GenericType<List<ValidationError>>() {{}});
	}

	public static String getValidationMessage(Response response, int num) {
		return response.readEntity(new GenericType<List<ValidationError>>() {{}}).get(num).getMessage();
	}

  public static int getValidationMessages(Response response) {
		return response.readEntity(new GenericType<List<ValidationError>>() {{}}).size();
  }

}

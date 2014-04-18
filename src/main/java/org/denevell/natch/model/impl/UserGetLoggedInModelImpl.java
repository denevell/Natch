package org.denevell.natch.model.impl;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.users.User;
import org.denevell.natch.model.interfaces.UserGetLoggedInModel;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class UserGetLoggedInModelImpl implements UserGetLoggedInModel {
	
	private WebTarget mServiceTarget;

	public UserGetLoggedInModelImpl() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		WebTarget target = client.target("http://localhost:8080/CoreUserService-ForAutomatedTests/");
		mServiceTarget = target.path("rest").path("user").path("get");
	}

	@Override
	public User get(Object authObject) {

		try {
			User u = mServiceTarget
					.request()
					.header("AuthKey", (String)authObject)
					.get(User.class);
			return u;
		} catch (Exception e) {
			return null;
		}
		
	}

}

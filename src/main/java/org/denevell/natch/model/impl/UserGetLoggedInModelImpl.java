package org.denevell.natch.model.impl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.denevell.natch.io.users.User;
import org.denevell.natch.model.interfaces.UserGetLoggedInModel;
import org.denevell.natch.utils.ManifestVars;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.jvnet.hk2.annotations.Service;

@Service
public class UserGetLoggedInModelImpl implements UserGetLoggedInModel {
	
	private WebTarget mServiceTarget;

	public UserGetLoggedInModelImpl() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		String uri = ManifestVars.getUserServiceUrl();
		WebTarget target = client.target(uri);
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

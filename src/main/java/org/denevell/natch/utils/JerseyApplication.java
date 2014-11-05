package org.denevell.natch.utils;

import org.denevell.natch.model.impl.DependencyInjectionBinder;
import org.denevell.userservice.AnnotationProcessor.UserService;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@UserService(persistenceUnitName = "PERSISTENCE_UNIT_NAME", servicePath = "/cus/*")
public class JerseyApplication extends ResourceConfig {
	public JerseyApplication() {
		register(JacksonFeature.class);
		register(new DependencyInjectionBinder());
	}
}

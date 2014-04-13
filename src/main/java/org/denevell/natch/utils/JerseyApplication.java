package org.denevell.natch.utils;

import org.denevell.natch.model.impl.DependencyInjectionBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class JerseyApplication extends ResourceConfig {
	public JerseyApplication() {
		register(JacksonFeature.class);
		register(new DependencyInjectionBinder());
	}
}

package org.denevell.natch.utils;

import org.denevell.natch.utils.UserGetLoggedInService.UserGetLoggedInModelImpl;
import org.denevell.userservice.AnnotationProcessor.UserService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@UserService(persistenceUnitName = "PERSISTENCE_UNIT_NAME", servicePath = "/cus/*")
public class JerseyApplication extends ResourceConfig {

  public static final String TestEntityManagerFactoryName = "test";
  public static final String ProdEntityManagerFactoryName = "prod";

  public JerseyApplication() {
    register(JacksonFeature.class);
    register(new DependencyInjectionBinder());
    property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
  }

  public static class DependencyInjectionBinder extends AbstractBinder {

    @Override
    protected void configure() {
      bind(UserGetLoggedInModelImpl.class).to(UserGetLoggedInService.class);
      //bind(ThreadFromPostServiceImpl.class).to(ThreadFromPostService.class);
    }
  }

}

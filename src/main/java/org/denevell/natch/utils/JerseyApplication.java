package org.denevell.natch.utils;

import org.denevell.natch.serv.PostAdd.PostAddService;
import org.denevell.natch.serv.PostDelete.PostDeleteService;
import org.denevell.natch.serv.PostEdit.PostEditService;
import org.denevell.natch.serv.ThreadFromPost.ThreadFromPostService;
import org.denevell.natch.serv.ThreadFromPost.ThreadFromPostServiceImpl;
import org.denevell.natch.serv.ThreadSingle.ThreadSingleService;
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
      bind(PostAddServiceImpl.class).to(PostAddService.class);
      bind(PostDeleteServiceImpl.class).to(PostDeleteService.class);
      bind(PostEditServiceImpl.class).to(PostEditService.class);
      bind(ThreadSingleServiceImpl.class).to(ThreadSingleService.class);
      bind(UserGetLoggedInModelImpl.class).to(UserGetLoggedInService.class);
      bind(ThreadFromPostServiceImpl.class).to(ThreadFromPostService.class);
    }
  }

  public static class PostAddServiceImpl implements PostAddService {};
  public static class PostDeleteServiceImpl implements PostDeleteService {};
  public static class PostEditServiceImpl implements PostEditService {};
  public static class ThreadSingleServiceImpl implements ThreadSingleService {};

}

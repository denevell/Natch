package org.denevell.natch.utils;

import org.denevell.natch.model.PostAddModel;
import org.denevell.natch.model.PostDeleteModel;
import org.denevell.natch.model.PostEditModel;
import org.denevell.natch.model.PostSingleModel;
import org.denevell.natch.model.PostsListByModDateModel;
import org.denevell.natch.model.PushAddModel;
import org.denevell.natch.model.PushListModel;
import org.denevell.natch.model.ThreadFromPostModel;
import org.denevell.natch.model.ThreadListModel;
import org.denevell.natch.model.ThreadsListModel;
import org.denevell.natch.model.UserGetLoggedInModel;
import org.denevell.natch.model.PostAddModel.PostAddModelImpl;
import org.denevell.natch.model.PostDeleteModel.PostDeleteModelImpl;
import org.denevell.natch.model.PostEditModel.PostEditModelImpl;
import org.denevell.natch.model.PostSingleModel.PostSingleModelImpl;
import org.denevell.natch.model.PostsListByModDateModel.PostsListByModDateModelImpl;
import org.denevell.natch.model.PushAddModel.PushAddModelImpl;
import org.denevell.natch.model.PushListModel.PushListModelImpl;
import org.denevell.natch.model.ThreadFromPostModel.ThreadFromPostModelImpl;
import org.denevell.natch.model.ThreadListModel.ThreadListModelImpl;
import org.denevell.natch.model.ThreadsListModel.ThreadsListModelImpl;
import org.denevell.natch.model.UserGetLoggedInModel.UserGetLoggedInModelImpl;
import org.denevell.userservice.AnnotationProcessor.UserService;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@UserService(persistenceUnitName = "PERSISTENCE_UNIT_NAME", servicePath = "/cus/*")
public class JerseyApplication extends ResourceConfig {
	public JerseyApplication() {
		register(JacksonFeature.class);
		register(MOXyJsonProvider.class);
		register(new DependencyInjectionBinder());
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
	}

public static class DependencyInjectionBinder extends AbstractBinder {
	@Override
	protected void configure() {
		bind(PostAddModelImpl.class).to(PostAddModel.class);
		bind(PostDeleteModelImpl.class).to(PostDeleteModel.class);
		bind(PostEditModelImpl.class).to(PostEditModel.class);
		bind(PostsListByModDateModelImpl.class).to(PostsListByModDateModel.class);
		bind(PostSingleModelImpl.class).to(PostSingleModel.class);
		bind(ThreadListModelImpl.class).to(ThreadListModel.class);
		bind(ThreadsListModelImpl.class).to(ThreadsListModel.class);
		bind(ThreadFromPostModelImpl.class).to(ThreadFromPostModel.class);
		bind(PushAddModelImpl.class).to(PushAddModel.class);
		bind(PushListModelImpl.class).to(PushListModel.class);
		bind(UserGetLoggedInModelImpl.class).to(UserGetLoggedInModel.class);
	}
}
}

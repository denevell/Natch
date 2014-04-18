package org.denevell.natch.model.impl;

import org.denevell.natch.model.interfaces.PostAddModel;
import org.denevell.natch.model.interfaces.PostDeleteModel;
import org.denevell.natch.model.interfaces.PostEditModel;
import org.denevell.natch.model.interfaces.PostSingleModel;
import org.denevell.natch.model.interfaces.PostsListByModDateModel;
import org.denevell.natch.model.interfaces.PushAddModel;
import org.denevell.natch.model.interfaces.PushListModel;
import org.denevell.natch.model.interfaces.ThreadFromPostModel;
import org.denevell.natch.model.interfaces.ThreadListModel;
import org.denevell.natch.model.interfaces.ThreadsListModel;
import org.denevell.natch.model.interfaces.UserGetLoggedInModel;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class DependencyInjectionBinder extends AbstractBinder {
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
package org.denevell.natch.model.impl;

import org.denevell.natch.model.interfaces.PostAddModel;
import org.denevell.natch.model.interfaces.PostDeleteModel;
import org.denevell.natch.model.interfaces.PostEditModel;
import org.denevell.natch.model.interfaces.PostSingleModel;
import org.denevell.natch.model.interfaces.PostsListByModDateModel;
import org.denevell.natch.model.interfaces.ThreadListModel;
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
	}
}
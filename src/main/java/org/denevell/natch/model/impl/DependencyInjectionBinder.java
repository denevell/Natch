package org.denevell.natch.model.impl;

import org.denevell.natch.model.interfaces.PostAddModel;
import org.denevell.natch.model.interfaces.PostDeleteModel;
import org.denevell.natch.model.interfaces.PostEditModel;
import org.denevell.natch.model.interfaces.PostSingleModel;
import org.denevell.natch.model.interfaces.PostsListByModDateModel;
import org.denevell.natch.model.interfaces.PushAddModel;
import org.denevell.natch.model.interfaces.PushListModel;
import org.denevell.natch.model.interfaces.ThreadListModel;
import org.denevell.natch.model.interfaces.ThreadFromPostModel;
import org.denevell.natch.model.interfaces.ThreadsListModel;
import org.denevell.natch.model.interfaces.UserAddModel;
import org.denevell.natch.model.interfaces.UserAdminToggleModel;
import org.denevell.natch.model.interfaces.UserChangePasswordModel;
import org.denevell.natch.model.interfaces.UserLoginModel;
import org.denevell.natch.model.interfaces.UserLogoutModel;
import org.denevell.natch.model.interfaces.UserRequestPasswordResetModel;
import org.denevell.natch.model.interfaces.UsersListModel;
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
		bind(UserAddModelImpl.class).to(UserAddModel.class);
		bind(UserAdminToggleModelImpl.class).to(UserAdminToggleModel.class);
		bind(UserChangePasswordModelImpl.class).to(UserChangePasswordModel.class);
		bind(UserLoginModelImpl.class).to(UserLoginModel.class);
		bind(UserLogoutModelImpl.class).to(UserLogoutModel.class);
		bind(UserRequestPasswordResetModelImpl.class).to(UserRequestPasswordResetModel.class);
		bind(UsersListModelImpl.class).to(UsersListModel.class);
	}
}
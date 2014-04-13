package org.denevell.natch.model.impl;

import javax.inject.Singleton;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.PostAddModel;
import org.denevell.natch.serv.post.ThreadFactory;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PostAddModelImpl implements PostAddModel {

	final ThreadFactory mThreadFactory = new ThreadFactory();

	@Override
	public ThreadEntity add(final PostEntity postEntity) {
		CallDbBuilder<ThreadEntity> model = new CallDbBuilder<ThreadEntity>();
		model.startTransaction();
		ThreadEntity thread = model.createOrUpdate(
				postEntity.getThreadId(),
				new CallDbBuilder.UpdateItem<ThreadEntity>() {
					@Override
					public ThreadEntity update(ThreadEntity item) {
						return mThreadFactory.makeThread(item, postEntity);
					}
				}, new CallDbBuilder.NewItem<ThreadEntity>() {
					@Override
					public ThreadEntity newItem() {
						return mThreadFactory.makeThread(postEntity);
					}
				}, ThreadEntity.class);
		model.commitAndCloseEntityManager();
		return thread;
	}

}
package org.denevell.natch.model.impl;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.PostAddModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service
public class PostAddModelImpl implements PostAddModel {

	final ThreadFactory mThreadFactory = new ThreadFactory();

	@Override
	public ThreadEntity add(final PostEntity postEntity) {
		Jrappy<ThreadEntity> model = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);
		model.startTransaction();
		ThreadEntity thread = model.createOrUpdate(
				postEntity.getThreadId(),
				new Jrappy.UpdateItem<ThreadEntity>() {
					@Override
					public ThreadEntity update(ThreadEntity item) {
						return mThreadFactory.makeThread(item, postEntity);
					}
				}, new Jrappy.NewItem<ThreadEntity>() {
					@Override
					public ThreadEntity newItem() {
						return mThreadFactory.makeThread(postEntity);
					}
				}, ThreadEntity.class);
		model.commitAndCloseEntityManager();
		return thread;
	}

}
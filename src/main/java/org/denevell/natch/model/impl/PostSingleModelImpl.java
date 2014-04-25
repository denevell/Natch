package org.denevell.natch.model.impl;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.PostSingleModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service
public class PostSingleModelImpl implements PostSingleModel {

	private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);
	private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);
	
	public PostEntity find(long id) {
		PostEntity post = mPostModel.startTransaction()
				.namedQuery(PostEntity.NAMED_QUERY_FIND_BY_ID)
				.queryParam("id", id).single(PostEntity.class);
		if (post == null) {
			return null;
		} else {
			ThreadEntity thread = mThreadModel
				.useTransaction(mPostModel.getEntityManager())
				.namedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID)
				.queryParam("id", post.getThreadId())
				.single(ThreadEntity.class);
			mThreadModel.commitAndCloseEntityManager();
			post.setSubject(thread.getRootPost().getSubject());
			return post;
		}
	}

}
package org.denevell.natch.model.impl;

import javax.inject.Singleton;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.PostSingleModel;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PostSingleModelImpl implements PostSingleModel {

	private CallDbBuilder<PostEntity> mPostModel = new CallDbBuilder<PostEntity>();
	private CallDbBuilder<ThreadEntity> mThreadModel = new CallDbBuilder<ThreadEntity>();
	
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
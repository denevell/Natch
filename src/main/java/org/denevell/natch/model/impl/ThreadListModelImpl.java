package org.denevell.natch.model.impl;

import java.util.List;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.ThreadListModel;

public class ThreadListModelImpl implements ThreadListModel {

	private CallDbBuilder<PostEntity> mPostModel = new CallDbBuilder<PostEntity>();
	private CallDbBuilder<ThreadEntity> mThreadModel = new CallDbBuilder<ThreadEntity>();

	@Override
	public ThreadAndPosts list(String id, int start, int maxNumPosts) {
		List<PostEntity> posts = null;
		ThreadEntity thread = null;
		posts = mPostModel
				.startTransaction()
				.start(start)
				.max(maxNumPosts)
				.namedQuery(PostEntity.NAMED_QUERY_FIND_BY_THREADID)
				.queryParam("threadId", id)
				.list(PostEntity.class);
		if(posts!=null)  {
			thread = mThreadModel 
				.useTransaction(mPostModel.getEntityManager())
				.namedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID)
				.queryParam("id", id)
				.single(ThreadEntity.class);
			mPostModel.commitAndCloseEntityManager();
		}
		if(posts==null || thread==null || posts.size()==0) {
			return null;
		} else {
			return new ThreadAndPosts(thread, posts);
		}
	}

}

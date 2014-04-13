package org.denevell.natch.model.impl;

import java.util.List;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.ThreadsListModel;

public class ThreadsListModelImpl implements ThreadsListModel {

	private CallDbBuilder<ThreadEntity> mThreadModel = new CallDbBuilder<ThreadEntity>();

	public ThreadsAndNumTotalThreads list(String tag, int start, int maxNumPosts) {
		String listNamedQuery;
		String countNamedQuery;
		if(tag!=null) {
			mThreadModel = mThreadModel.queryParam("tag", tag);
			listNamedQuery = ThreadEntity.NAMED_QUERY_LIST_THREADS_BY_TAG;
			countNamedQuery = ThreadEntity.NAMED_QUERY_COUNT_THREAD_BY_TAG;
		} else {
			listNamedQuery = ThreadEntity.NAMED_QUERY_LIST_THREADS;
			countNamedQuery = ThreadEntity.NAMED_QUERY_COUNT_THREADS;
		}
		List<ThreadEntity> threads = mThreadModel
				.startTransaction()
				.start(start)
				.max(maxNumPosts)
				.namedQuery(listNamedQuery)
				.list(ThreadEntity.class);
		if(threads==null) return null;
		long num = -1;
		num = mThreadModel
				.useTransaction(mThreadModel.getEntityManager())
				.namedQuery(countNamedQuery)
				.count();
		mThreadModel.closeEntityManager();
		return new ThreadsAndNumTotalThreads(threads, num);
	}

}

package org.denevell.natch.model.impl;

import java.util.List;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.ThreadsListModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service
public class ThreadsListModelImpl implements ThreadsListModel {

	private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);

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

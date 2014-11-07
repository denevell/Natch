package org.denevell.natch.model;

import java.util.List;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface ThreadsListModel {
  public static class ThreadsAndNumTotalThreads {
    private long numOfThreads;
    private List<ThreadEntity> threads;

    public ThreadsAndNumTotalThreads(List<ThreadEntity> threadPosts, long numOfThreads) {
      this.numOfThreads = numOfThreads;
      this.threads = threadPosts;
    }

    public long getNumOfThreads() {
      return numOfThreads;
    }

    public void setNumOfThreads(long numOfThreads) {
      this.numOfThreads = numOfThreads;
    }

    public List<ThreadEntity> getThreads() {
      return threads;
    }

    public void setThreads(List<ThreadEntity> threads) {
      this.threads = threads;
    }
  }

  ThreadsAndNumTotalThreads list(String tag, int start, int maxNumPosts);

  @Service
  public static class ThreadsListModelImpl implements ThreadsListModel {

    private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);

    public ThreadsAndNumTotalThreads list(String tag, int start, int maxNumPosts) {
      try {
        String listNamedQuery;
        String countNamedQuery;
        if (tag != null) {
          mThreadModel = mThreadModel.queryParam("tag", tag);
          listNamedQuery = ThreadEntity.NAMED_QUERY_LIST_THREADS_BY_TAG;
          countNamedQuery = ThreadEntity.NAMED_QUERY_COUNT_THREAD_BY_TAG;
        } else {
          listNamedQuery = ThreadEntity.NAMED_QUERY_LIST_THREADS;
          countNamedQuery = ThreadEntity.NAMED_QUERY_COUNT_THREADS;
        }
        List<ThreadEntity> threads = mThreadModel.startTransaction().start(start).max(maxNumPosts).namedQuery(listNamedQuery)
            .list(ThreadEntity.class);
        if (threads == null)
          return null;
        long num = -1;
        num = mThreadModel.useTransaction(mThreadModel.getEntityManager()).namedQuery(countNamedQuery).count();
        return new ThreadsAndNumTotalThreads(threads, num);
      } finally {
        mThreadModel.commitAndCloseEntityManager();
      }
    }

  }
}

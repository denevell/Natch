package org.denevell.natch.model.interfaces;

import java.util.List;

import org.denevell.natch.db.entities.ThreadEntity;
import org.glassfish.jersey.spi.Contract;

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
}

package org.denevell.natch.model.interfaces;

import java.util.List;

import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ThreadListModel {
	public static class ThreadAndPosts {
		private ThreadEntity threadEntity;
		private List<PostEntity> posts;
		public ThreadAndPosts(ThreadEntity thread, List<PostEntity> threadPosts) {
			setThreadEntity(thread);
			setPosts(threadPosts);
		}
		public ThreadEntity getThreadEntity() {
			return threadEntity;
		}
		public void setThreadEntity(ThreadEntity threadEntity) {
			this.threadEntity = threadEntity;
		}
		public List<PostEntity> getPosts() {
			return posts;
		}
		public void setPosts(List<PostEntity> posts) {
			this.posts = posts;
		}
	}
	ThreadAndPosts list(String threadId, int start, int maxNumPosts);
}

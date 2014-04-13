package org.denevell.natch.model.impl;

import javax.inject.Singleton;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.db.CallDbBuilder.DeleteOrMerge;
import org.denevell.natch.db.ThreadFactory;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.ThreadFromPostModel;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class ThreadFromPostModelImpl implements ThreadFromPostModel {

	private CallDbBuilder<PostEntity> mPostModel = new CallDbBuilder<PostEntity>();
	private CallDbBuilder<ThreadEntity> mThreadModel = new CallDbBuilder<ThreadEntity>();

	@Override
	public ThreadEntity makeNewThread(long postId, String subject) {
		PostEntity post = mPostModel
				.startTransaction()
				.find(postId, true, PostEntity.class);
		if(post==null) return null;
		PostEntity newPost = new PostEntity(post);
		newPost.setId(0);
		newPost.adminEdited();
		newPost.setThreadId(null);
		newPost.setSubject(subject);
		removePostFromOldThread(post);
		mPostModel.getEntityManager().remove(post);
		ThreadEntity thread = createNewThreadFromPost(newPost);
		mPostModel.commitAndCloseEntityManager();
		return thread;
	}

	private void removePostFromOldThread(final PostEntity post) {
		mThreadModel.useTransaction(mPostModel.getEntityManager())
			.findAndUpdateOrDelete(post.getThreadId(), 
				new DeleteOrMerge<ThreadEntity>() {
					@Override public boolean shouldDelete(ThreadEntity item) {
						item.updateThreadToRemovePost(post);
						return item.getPosts()==null || item.getPosts().size()==0;
					}
				}, 
				ThreadEntity.class);
	}

	private ThreadEntity createNewThreadFromPost(final PostEntity post) {
		ThreadEntity thread = mThreadModel
			.useTransaction(mPostModel.getEntityManager())
			.createOrUpdate(
				null,
				new CallDbBuilder.UpdateItem<ThreadEntity>() {
					@Override public ThreadEntity update(ThreadEntity item) {
							return null;
						}
					}, new CallDbBuilder.NewItem<ThreadEntity>() {
						@Override public ThreadEntity newItem() {
							return new ThreadFactory().makeThread(post);
						}
					}, ThreadEntity.class);
		return thread;
	}

}
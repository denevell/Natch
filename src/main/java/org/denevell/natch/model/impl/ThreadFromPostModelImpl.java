package org.denevell.natch.model.impl;

import org.denevell.jrappy.Jrappy;
import org.denevell.jrappy.Jrappy.DeleteOrMerge;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.entities.ThreadEntity;
import org.denevell.natch.model.interfaces.ThreadFromPostModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service
public class ThreadFromPostModelImpl implements ThreadFromPostModel {

	private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);
	private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);

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
				new Jrappy.UpdateItem<ThreadEntity>() {
					@Override public ThreadEntity update(ThreadEntity item) {
							return null;
						}
					}, new Jrappy.NewItem<ThreadEntity>() {
						@Override public ThreadEntity newItem() {
							return new ThreadFactory().makeThread(post);
						}
					}, ThreadEntity.class);
		return thread;
	}

}
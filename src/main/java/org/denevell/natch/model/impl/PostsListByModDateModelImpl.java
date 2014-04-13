package org.denevell.natch.model.impl;

import java.util.List;

import javax.inject.Singleton;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.interfaces.PostsListByModDateModel;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PostsListByModDateModelImpl implements PostsListByModDateModel {

	private CallDbBuilder<PostEntity> mPostModel = new CallDbBuilder<PostEntity>();
	
	@Override
	public List<PostEntity> list(int start, int numResults) {
		List<PostEntity> posts = mPostModel 
				.startTransaction()
				.start(start)
				.max(numResults)
				.namedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE)
				.list(PostEntity.class);
		mPostModel.commitAndCloseEntityManager();
		return posts;
	}

}
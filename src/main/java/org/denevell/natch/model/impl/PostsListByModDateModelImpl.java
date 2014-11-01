package org.denevell.natch.model.impl;

import java.util.List;

import javax.inject.Singleton;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.PostEntity;
import org.denevell.natch.model.interfaces.PostsListByModDateModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PostsListByModDateModelImpl implements PostsListByModDateModel {

	private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);
	
	@Override
	public List<PostEntity> list(int start, int numResults) {
	  try  {
		List<PostEntity> posts = mPostModel 
				.startTransaction()
				.start(start)
				.max(numResults)
				.namedQuery(PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE)
				.list(PostEntity.class);
		return posts;
	  } finally {
	    mPostModel.commitAndCloseEntityManager();
	  }
	}

}
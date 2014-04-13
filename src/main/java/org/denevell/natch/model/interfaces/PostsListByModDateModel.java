package org.denevell.natch.model.interfaces;

import java.util.List;

import org.denevell.natch.model.entities.PostEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface PostsListByModDateModel {
	List<PostEntity>  list(int start, int numResults);
}

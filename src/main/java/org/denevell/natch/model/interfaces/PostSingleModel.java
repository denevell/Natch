package org.denevell.natch.model.interfaces;

import org.denevell.natch.model.entities.PostEntity;

public interface PostSingleModel {
	/**
	 * @param id
	 * @return PostEntity with the subject of the thread
	 */
	PostEntity find(long id);
}

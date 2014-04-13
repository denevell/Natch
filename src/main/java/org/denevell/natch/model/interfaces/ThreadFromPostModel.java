package org.denevell.natch.model.interfaces;

import org.denevell.natch.db.entities.ThreadEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ThreadFromPostModel {
	public ThreadEntity makeNewThread(long postId, String subject);
}

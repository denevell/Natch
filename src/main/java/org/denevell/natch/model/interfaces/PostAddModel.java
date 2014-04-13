package org.denevell.natch.model.interfaces;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface PostAddModel {
	ThreadEntity add(PostEntity postEntity);
}

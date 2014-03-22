package org.denevell.natch.db.adapters;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;

public class AddPostRequestToPostEntity {

	public static PostEntity adapt(
			AddPostResourceInput input,
			boolean adminEdited, 
			UserEntity entityUser) {
		PostEntity pe = new PostEntity();
		long created = new Date().getTime();
		pe.setContent(input.getContent());
		pe.setSubject(input.getSubject());
		pe.setThreadId(input.getThreadId());
		pe.setTags(input.getTags());
		pe.setUser(entityUser);
		pe.setCreated(created);
		pe.setModified(created);
		if (adminEdited) {
			pe.adminEdited();
		}
		return pe;
	}

}

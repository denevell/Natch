package org.denevell.natch.db.adapters;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.io.posts.AddPostResourceInput;

public class AddPostRequestToPostEntity extends PostEntity {

	public AddPostRequestToPostEntity(
			AddPostResourceInput input,
			boolean adminEdited, 
			UserEntity entityUser) {
		long created = new Date().getTime();
		setContent(input.getContent());
		setSubject(input.getSubject());
		setThreadId(input.getThreadId());
		setTags(input.getTags());
		setUser(entityUser);
		setCreated(created);
		setModified(created);
		if (adminEdited) {
			adminEdited();
		}
	}

}

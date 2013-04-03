package org.denevell.natch.tests;

import static org.junit.Assert.*;

import org.denevell.natch.serv.posts.PostsModel;
import org.denevell.natch.serv.posts.PostsModel.AddPostResult;
import org.junit.Before;
import org.junit.Test;

public class AddPostModelTests {
	
	private PostsModel model;

	@Before
	public void setup() {
		model = new PostsModel();
	}
	
	@Test
	public void shouldMakePost() {
		// Arrange
		String content = "Some content";
		String subject = "Some subject";
		
		// Act
		AddPostResult result = model.addPost(subject, content);
		
		// Assert
		assertEquals(AddPostResult.SUCCESS, result);
	}
}
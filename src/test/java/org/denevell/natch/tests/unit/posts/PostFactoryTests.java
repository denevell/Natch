package org.denevell.natch.tests.unit.posts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.serv.posts.PostFactory;
import org.junit.Before;
import org.junit.Test;

public class PostFactoryTests {
	
	private PostFactory factory;

	@Before
	public void setup() {
	}
	
	@Test
	public void shouldMakePost() {
		// Arrange
		factory = new PostFactory();
		long time = new Date().getTime();
		
		// Act
		PostEntity p = factory.createPost("sub", "conte");
		
		// Assert
		assertEquals("sub", p.getSubject());
		assertEquals("conte", p.getContent());
		assertTrue("Check the created time", p.getCreated() >= time && p.getCreated() <= time+3000);
		assertTrue("Check the modified time", p.getModified() >= time && p.getModified() <= time+3000);
	}
}
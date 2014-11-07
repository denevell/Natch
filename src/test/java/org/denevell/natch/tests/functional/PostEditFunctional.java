package org.denevell.natch.tests.functional;


public class PostEditFunctional {
	
  /*
	private WebTarget service;
	private LoginResourceReturnData loginResult;
	private AddPostResourceInput initalInput;
	private PostResource initialPost;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private ListPostsResource originallyListedPosts;
    private String authKey;
	private RegisterPO registerPo;
	private AddPostPO addPostPo;

	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
	    registerPo = new RegisterPO();
	    addPostPo = new AddPostPO();

	    registerPo.register("aaron@aaron.com", "passy");
		loginResult = new LoginPO().login("aaron@aaron.com", "passy");
	    authKey = loginResult.getAuthKey();

		// Add post
		initalInput = new AddPostResourceInput("sub", "cont");
		initalInput.setTags(Arrays.asList(new String[] {"tag1", "tag2"}));
		addPostPo.add("sub", "cont", authKey);
		originallyListedPosts = PostsListFunctional.listRecentPostsThreads(service);
		// save it
		initialPost = originallyListedPosts.getPosts().get(0);
		// Pre assert
		assertEquals(initalInput.getContent(), initialPost.getContent());
		assertEquals(initalInput.getSubject(), initialPost.getSubject());
	}
	
	@Test
	public void shouldEditPost() {
		// Arrange
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("sup");
		
		// Act - edit then list
		EditPostResourceReturnData editReturnData = editPost(service, authKey, initialPost.getId(), editedInput); 		
		ListPostsResource newListedPosts = PostsListFunctional.listRecentPostsThreads(service);
		
		// Assert
		assertEquals("", editReturnData.getError());
		assertTrue(editReturnData.isSuccessful());		
		assertEquals(initialPost.getId(), newListedPosts.getPosts().get(0).getId());
		assertEquals(initialPost.getCreation(), newListedPosts.getPosts().get(0).getCreation());
		assertEquals(initialPost.getUsername(), newListedPosts.getPosts().get(0).getUsername());
		assertEquals(initialPost.threadId, newListedPosts.getPosts().get(0).threadId);
		assertEquals("sup", newListedPosts.getPosts().get(0).getContent());
		assertFalse("Edit as admin not set", newListedPosts.getPosts().get(0).isAdminEdited());
		//assertEquals("sup two?", newListedPosts.getPosts().get(0).getSubject());
		assertTrue(newListedPosts.getPosts().get(0).getModification() > initialPost.getModification());
	}

    public static EditPostResourceReturnData editPost(WebTarget service, String authKey, long postId, EditPostResource editedInput) {
        return service
		.path("rest").path("post").path("editpost")
		.path(String.valueOf(postId)).request()
		.header("AuthKey", authKey)
    	.post(Entity.entity(editedInput, MediaType.APPLICATION_JSON),EditPostResourceReturnData.class);
    }
	
	@Test
	public void shouldSeeErrorOnUnAuthorised() {
		// Arrange
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("sup");
		// Login with another user
	    registerPo.register("aaron1@aaron.com", "passy");
		LoginResourceReturnData loginResult1 = new LoginPO().login("aaron1@aaron.com", "passy");
		
		// Act - edit with different user then list
		EditPostResourceReturnData editReturnData = editPost(service, loginResult1.getAuthKey(), initialPost.getId(), editedInput); 		
		ListPostsResource newListedPosts = PostsListFunctional.listRecentPostsThreads(service);
		
		// Assert
		assertEquals(rb.getString(Strings.post_not_yours), editReturnData.getError());
		assertFalse(editReturnData.isSuccessful());		
		assertEquals(initalInput.getContent(), newListedPosts.getPosts().get(0).getContent());
		assertEquals(initalInput.getSubject(), newListedPosts.getPosts().get(0).getSubject());
	}

	@Test
	public void shouldEditAsAdmin() {
		// Arrange
		// Login with another user
	    registerPo.register("aaron1@aaron.com", "passy");
		LoginResourceReturnData loginResult1 = new LoginPO().login("aaron1@aaron.com", "passy");

        // Act - Add a post as new user
        addPostPo.add("presubadminedit", "precontadminedit", new String[] {"tag1", "tag2"}, loginResult1.getAuthKey());
		ListPostsResource originalPosts = PostsListFunctional.listRecentPostsThreads(service);
		PostResource addedPost = originalPosts.getPosts().get(0);
		
		// Act - edit with first, admin, user
		EditPostResource editedInput = new EditPostResource();
		editedInput.setContent("supadmineditedcontent");
		EditPostResourceReturnData editReturnData = editPost(service, loginResult.getAuthKey(), addedPost.getId(), editedInput); 		
		ListPostsResource newListedPosts = PostsListFunctional.listRecentPostsThreads(service);
		PostResource editPost = newListedPosts.getPosts().get(0);
		
		// Assert
		assertEquals("", editReturnData.getError());
		assertTrue(editReturnData.isSuccessful());		
		assertEquals("supadmineditedcontent", editPost.getContent());
		assertEquals("aaron1@aaron.com", editPost.getUsername());
		assertFalse("Edited as admin flag not set originally", addedPost.isAdminEdited());
		assertTrue("Edited as admin flag set", editPost.isAdminEdited());
	}
	
	@Test
	public void shouldSeeErrorOnBlankContent() {
		try {
			EditPostResource editedInput = new EditPostResource();
			editedInput.setContent(" ");
			editPost(service, loginResult.getAuthKey(), initialPost.getId(), editedInput); 		
			assertFalse("Excepted 400 error", true);
		} catch(WebApplicationException e) {
			assertEquals(400, e.getResponse().getStatus());
			return;
		}	
		
		ListPostsResource newListedPosts = PostsListFunctional.listRecentPostsThreads(service);
		assertEquals(initalInput.getContent(), newListedPosts.getPosts().get(0).getContent());
		assertEquals(initalInput.getSubject(), newListedPosts.getPosts().get(0).getSubject());
	}
	*/
	
}

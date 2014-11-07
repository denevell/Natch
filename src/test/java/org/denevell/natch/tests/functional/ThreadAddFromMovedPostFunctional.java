package org.denevell.natch.tests.functional;


public class ThreadAddFromMovedPostFunctional {
	
  /*
	private WebTarget service;
    ResourceBundle rb = Strings.getMainResourceBundle();
	private LoginResourceReturnData adminLoginResult;
	private RegisterPO registerPo;
	private AddPostPO addPostPo;
	private LoginPO loginPo;
	private AddThreadFromPostPO addThreadFromPostPo;
	
	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		registerPo = new RegisterPO();
		loginPo = new LoginPO();
		addPostPo = new AddPostPO();
		addThreadFromPostPo = new AddThreadFromPostPO(service);
		TestUtils.deleteTestDb();
	    new RegisterPO().register("aaron", "aaron");
		adminLoginResult = new LoginPO().login("aaron", "aaron");
	}
	
	@Test
	public void shouldMakeThreadFromPost() {
	    registerPo.register("other", "other");
		LoginResourceReturnData loginResult = loginPo.login("other", "other");
		AddPostResourceReturnData threadRet = addPostPo.add("New thread", "first post", loginResult.getAuthKey());
		addPostPo.add("-", "Second post", loginResult.getAuthKey(), threadRet.getThread().getId());
		ListPostsResource posts = PostsListFunctional.listRecentPostsThreads(service);
		assertTrue("Should have two posts, thread starter and first post", posts.getPosts().size()==2);
		
        // Act
		AddPostResourceReturnData returnData = 
				addThreadFromPostPo.addThreadFromPost(
						"New subject",
						posts.getPosts().get(0).getId(),
						adminLoginResult.getAuthKey()); 
		
		// Assert
		assertTrue(returnData.isSuccessful());
		posts = PostsListFunctional.listRecentPostsThreads(service);
		assertEquals("New thread new new subject", posts.getPosts().get(0).getSubject(), "New subject");
		assertEquals("New thread has old user id", posts.getPosts().get(0).getUsername(), "other");
		assertTrue("New thread is marked edited by admin", posts.getPosts().get(0).isAdminEdited());
		assertTrue("Still just have two posts, since one's been moved", posts.getPosts().size()==2);
	}

	@Test
	public void shouldThrow401WhenNotAdmin() {
	    registerPo.register("other", "other");
		LoginResourceReturnData loginResult = 
				loginPo.login("other", "other");
		AddPostResourceReturnData threadRet = 
				addPostPo.add("c", "s", loginResult.getAuthKey());
		addPostPo.add("-", "b", loginResult.getAuthKey(), threadRet.getThread().getId());
		ListPostsResource posts = PostsListFunctional.listRecentPostsThreads(service);
		
    // Act
		try {
				addThreadFromPostPo.addThreadFromPost(
						"New subject",
						posts.getPosts().get(0).getId(),
						loginResult.getAuthKey()); 
        } catch (WebApplicationException e) {
            assertTrue(e.getResponse().getStatus()==401);
            return;
        }
		assertFalse("Was exception 401 exception", true);
	}
	*/
	
}

package org.denevell.natch.tests.unit.posts;


public class ShowPostRequestTests {
	
  /*
  ResourceBundle rb = Strings.getMainResourceBundle();
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Before
	public void setup() {
		response = mock(HttpServletResponse.class);
		request = mock(HttpServletRequest.class);

	}
	
	@Test
	public void shouldFindSinglePost() throws IOException {
		// Arrange
		PostEntity postEntity = new PostEntity("u1", 1, 1, "s1", "c1", null);
		List<String> asList = Arrays.asList(new String[] {"tag1"});
		postEntity.setTags(asList);
		postEntity.setId(400);
		postEntity.setThreadId("1234");
		postEntity.setSubject("thread_subject");
		PostSingleModel model = mock(PostSingleModel.class);
		Mockito.when(model.find(0)).thenReturn(postEntity);
		PostSingleRequest resourceShow = new PostSingleRequest(
				model,
				request, 
				response);
		
		// Act
		PostResource result = resourceShow.findById(0);
		
		// Assert
		assertEquals(1, result.getCreation());
		assertEquals(1, result.getModification());
		assertEquals("1234", result.threadId);
		assertEquals("u1", result.getUsername());
		assertEquals("thread_subject", result.getSubject());
		assertEquals("c1", result.getContent());
		assertEquals("tag1", result.getTags().get(0));
		assertEquals(400, result.getId());
	}	
	*/
	
}
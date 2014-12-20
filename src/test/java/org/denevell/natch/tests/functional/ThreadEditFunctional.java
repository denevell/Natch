package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.entities.PostEntity.Output;
import org.denevell.natch.tests.functional.pageobjects.PostsListPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadAddPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadEditPO;
import org.denevell.natch.tests.functional.pageobjects.ThreadsListPO;
import org.denevell.natch.tests.functional.pageobjects.UserLoginPO;
import org.denevell.natch.tests.functional.pageobjects.UserRegisterPO;
import org.denevell.userservice.serv.LoginRequest.LoginResourceReturnData;
import org.junit.Before;
import org.junit.Test;


public class ThreadEditFunctional {
    
    private LoginResourceReturnData loginResult;
    private PostsListPO postListPo;
    private ThreadAddPO threadAddPo;
    private ThreadEditPO threadEditPo;
    private ThreadsListPO threadListPo;

  @Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		threadAddPo = new ThreadAddPO();
		threadListPo = new ThreadsListPO();
		postListPo = new PostsListPO();
		threadEditPo = new ThreadEditPO();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	}

	@Test
	public void shouldEditThread() {
	  assertEquals(200, threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey()).getStatus());
		Output post = postListPo.list("0", "10").get(0);
		Response ret = threadEditPo.edit("sup1", "sup2", Lists.newArrayList("sup3"), post.id, loginResult.getAuthKey());

		assertEquals(200, ret.getStatus());
		org.denevell.natch.entities.ThreadEntity.Output thread = threadListPo.byThread("thread", 0, 10);

		assertEquals("sup1", thread.subject);
		assertEquals("sup2", thread.posts.get(0).content);
		assertEquals("sup3", thread.posts.get(0).tags.get(0));
	}

	@Test
	public void shouldSee401OnEditThreadWithWrongAuth() {
	  assertEquals(200, threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey()).getStatus());
		Output post = postListPo.list("0", "10").get(0);
		Response ret = threadEditPo.edit("sup1", "sup2", post.id, loginResult.getAuthKey()+"BAD");

		assertEquals(401, ret.getStatus());
	}
	
	@Test
	public void shouldSeeErrorOnBlankSubject() {
	  assertEquals(200, 
	      threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey()).getStatus());
		Output post = postListPo.list("0", "10").get(0);
		Response ret = threadEditPo.edit(" ", "dsfd", post.id, loginResult.getAuthKey());
		assertEquals(400, ret.getStatus());
		assertEquals("Subject cannot be blank", TestUtils.getValidationMessage(ret, 0));

		post = postListPo.list("0", "10").get(0);
		assertEquals("threadc", post.content);
		assertEquals("thread", post.subject);
	}

	@Test
	public void shouldSeeErrorOnBlankContent() {
		threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey());
		Output post = postListPo.list("0", "10").get(0);
		Response ret = threadEditPo.edit("xdfsdf", " ", post.id, loginResult.getAuthKey());
		assertEquals(400, ret.getStatus());
		assertEquals("Content cannot be blank", TestUtils.getValidationMessage(ret, 0));

		post = postListPo.list("0", "10").get(0);
		assertEquals("threadc", post.content);
		assertEquals("thread", post.subject);
	}

	@Test
	public void shouldSeeErrorOnLargeSubject() {
		threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey());
		Output post = postListPo.list("0", "10").get(0);
		Response ret = threadEditPo.edit("sdfsdfassdfklasjdflksdfkjasfl;kjasdl;kfjsd;lfjasdl;fjsal;fjas;ldfjasld;fjasl;fjasl;fjsal;dfjsdlfkjasdjf;lkasjf;lajsdfl;ajsdf;ljasdf;lkjsdlfjasd;lkfjasl;dfkjasdlfjasdlfkjasdlfjsadlkfjasldfkjsadlfkjlkdfj;alskdfjasl;kdfjasl;dfj;alsdfjal;skdfj;alsdkjf;slajdflk;asjflkasdjflasjflkajdflkasdjflksdjflkasdjflkasjdflkasdjf;lasdjf;lasjdf;lkasdjfl;sjadfl;asjdfl;asjdf;lasjdfxdfsdf", "cont", post.id, loginResult.getAuthKey());
		assertEquals(400, ret.getStatus());
		assertEquals("Subject cannot be more than 300 characters", TestUtils.getValidationMessage(ret, 0));

		post = postListPo.list("0", "10").get(0);
		assertEquals("threadc", post.content);
		assertEquals("thread", post.subject);
	}

	@Test
	public void shouldSeeErrorOnLargeTag() {
		threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey());
		Output post = postListPo.list("0", "10").get(0);
		Response ret = threadEditPo.edit("fxdfsdf", "cont", 
		    Lists.newArrayList("dslkjfaslkfjasd;ljfas;lkfjas;ljfas;lfjasld;fjsdlfkj"),
		    post.id, loginResult.getAuthKey());
		assertEquals(400, ret.getStatus());
		assertEquals("Tag cannot be more than 20 characters", TestUtils.getValidationMessage(ret, 0));

		post = postListPo.list("0", "10").get(0);
		assertEquals("threadc", post.content);
		assertEquals("thread", post.subject);
	}

}

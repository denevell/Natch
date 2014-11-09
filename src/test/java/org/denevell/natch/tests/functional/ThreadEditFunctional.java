package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import org.denevell.natch.model.PostEntity.Output;
import org.denevell.natch.tests.functional.pageobjects.PostAddPO;
import org.denevell.natch.tests.functional.pageobjects.PostDeletePO;
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
    
	private WebTarget service;
    private String authKey;
    private LoginResourceReturnData loginResult;
    private ThreadsListPO threadsListPo;
    private PostsListPO postListPo;
    private PostDeletePO postDeletePo;
    private PostAddPO postAddPo;
    private ThreadAddPO threadAddPo;
    private ThreadEditPO threadEditPo;

    @Before
	public void setup() throws Exception {
		TestUtils.deleteTestDb();
		threadAddPo = new ThreadAddPO();
		postAddPo = new PostAddPO();
		postDeletePo = new PostDeletePO();
		postListPo = new PostsListPO();
		threadEditPo = new ThreadEditPO();
		threadsListPo = new ThreadsListPO();
	  new UserRegisterPO().register("aaron@aaron.com", "passy");
		loginResult = new UserLoginPO().login("aaron@aaron.com", "passy");
	}
	
	@Test
	public void shouldSeeErrorOnBlankSubject() {
	  assertEquals(200, 
	      threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey()).getStatus());
		Output post = postListPo.list("0", "10").posts.get(0);
		Response ret = threadEditPo.edit(" ", "dsfd", post.id, loginResult.getAuthKey());
		assertEquals(400, ret.getStatus());
		assertEquals("Subject cannot be blank", TestUtils.getValidationMessage(ret, 0));

		post = postListPo.list("0", "10").posts.get(0);
		assertEquals("threadc", post.content);
		assertEquals("thread", post.subject);
	}

	@Test
	public void shouldSeeErrorOnBlankContent() {
		threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey());
		Output post = postListPo.list("0", "10").posts.get(0);
		Response ret = threadEditPo.edit("xdfsdf", " ", post.id, loginResult.getAuthKey());
		assertEquals(400, ret.getStatus());
		assertEquals("Content cannot be blank", TestUtils.getValidationMessage(ret, 0));

		post = postListPo.list("0", "10").posts.get(0);
		assertEquals("threadc", post.content);
		assertEquals("thread", post.subject);
	}

	@Test
	public void shouldSeeErrorOnLargeSubject() {
		threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey());
		Output post = postListPo.list("0", "10").posts.get(0);
		Response ret = threadEditPo.edit("sdfsdfassdfklasjdflksdfkjasfl;kjasdl;kfjsd;lfjasdl;fjsal;fjas;ldfjasld;fjasl;fjasl;fjsal;dfjsdlfkjasdjf;lkasjf;lajsdfl;ajsdf;ljasdf;lkjsdlfjasd;lkfjasl;dfkjasdlfjasdlfkjasdlfjsadlkfjasldfkjsadlfkjlkdfj;alskdfjasl;kdfjasl;dfj;alsdfjal;skdfj;alsdkjf;slajdflk;asjflkasdjflasjflkajdflkasdjflksdjflkasdjflkasjdflkasdjf;lasdjf;lasjdf;lkasdjfl;sjadfl;asjdfl;asjdf;lasjdfxdfsdf", "cont", post.id, loginResult.getAuthKey());
		assertEquals(400, ret.getStatus());
		assertEquals("Subject cannot be more than 300 characters", TestUtils.getValidationMessage(ret, 0));

		post = postListPo.list("0", "10").posts.get(0);
		assertEquals("threadc", post.content);
		assertEquals("thread", post.subject);
	}

	@Test
	public void shouldSeeErrorOnLargeTag() {
		threadAddPo.add("thread", "threadc", "thread", loginResult.getAuthKey());
		Output post = postListPo.list("0", "10").posts.get(0);
		Response ret = threadEditPo.edit("fxdfsdf", "cont", 
		    Lists.newArrayList("dslkjfaslkfjasd;ljfas;lkfjas;ljfas;lfjasld;fjsdlfkj"),
		    post.id, loginResult.getAuthKey());
		assertEquals(400, ret.getStatus());
		assertEquals("Tag cannot be more than 20 characters", TestUtils.getValidationMessage(ret, 0));

		post = postListPo.list("0", "10").posts.get(0);
		assertEquals("threadc", post.content);
		assertEquals("thread", post.subject);
	}

}

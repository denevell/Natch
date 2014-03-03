package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import org.denevell.natch.serv.push.PushResource;
import org.denevell.natch.tests.functional.pageobjects.PushIdsPO;
import org.denevell.natch.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class PushIdsFunctional {
	
    private WebResource service;
	private PushIdsPO addPushIdsPo;
	
	@Before
	public void setup() throws Exception {
		service = TestUtils.getRESTClient();
		TestUtils.deleteTestDb();
		addPushIdsPo = new PushIdsPO(service);
	}
	
	@Test
	public void shouldAddPushId() {
		addPushIdsPo.add("pushId1");
		addPushIdsPo.add("pushId2");
		
		PushResource ids = addPushIdsPo.list(); 
		assertEquals(2, ids.getIds().size());
		assertEquals("pushId1", ids.getIds().get(0).getClientId());
		assertEquals("pushId2", ids.getIds().get(1).getClientId());
	}

	@Test
	public void shouldntAddDuplicated() {
		addPushIdsPo.add("pushId1");
		addPushIdsPo.add("pushId1");
		
		PushResource ids = addPushIdsPo.list(); 
		assertEquals(1, ids.getIds().size());
		assertEquals("pushId1", ids.getIds().get(0).getClientId());
	}

}
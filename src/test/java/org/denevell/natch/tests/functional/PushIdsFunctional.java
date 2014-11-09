package org.denevell.natch.tests.functional;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.WebTarget;

import org.denevell.natch.model.PushEntity.Output;
import org.denevell.natch.tests.functional.pageobjects.PushIdsPO;
import org.junit.Before;
import org.junit.Test;

public class PushIdsFunctional {
	
  private WebTarget service;
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
		
		Output ids = addPushIdsPo.list(); 
		assertEquals(2, ids.ids.size());
		assertEquals("pushId1", ids.ids.get(0).clientId);
		assertEquals("pushId2", ids.ids.get(1).clientId);
	}

	@Test
	public void shouldntAddDuplicated() {
		addPushIdsPo.add("pushId1");
		addPushIdsPo.add("pushId1");
		
		Output ids = addPushIdsPo.list(); 
		assertEquals(1, ids.ids.size());
		assertEquals("pushId1", ids.ids.get(0).clientId);
	}

}

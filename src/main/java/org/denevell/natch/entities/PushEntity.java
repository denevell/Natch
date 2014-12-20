package org.denevell.natch.entities;

import java.util.ArrayList;

public class PushEntity {
	
	public static String NAMED_QUERY_FIND_ID = "findId";
	public static String NAMED_QUERY_LIST_IDS = "listIds";
	
	public String clientId;
	
	
	public static class AddInput {
	  public String id;
	  
	  public PushEntity adapt() {
	    PushEntity pushEntity = new PushEntity();
	    pushEntity.clientId = id;
      return pushEntity;
	  }
	}

  @SuppressWarnings("serial")
  public static class Output extends ArrayList<PushEntity> {}

}

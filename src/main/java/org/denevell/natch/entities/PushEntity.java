package org.denevell.natch.entities;

import java.util.ArrayList;

public class PushEntity {
	
	public static String NAMED_QUERY_FIND_ID = "findId";
	public static String NAMED_QUERY_LIST_IDS = "listIds";
	
	public String clientId;
	
	
	public static class AddInput {
	  public String id;
	}

  public static class Output {
    public ArrayList<PushEntity> ids = new ArrayList<PushEntity>();
  }

}

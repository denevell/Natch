package org.denevell.natch.entities;

import java.util.ArrayList;

import org.denevell.natch.utils.Adapter;

public class PushEntity {
	
	public static String NAMED_QUERY_FIND_ID = "findId";
	public static String NAMED_QUERY_LIST_IDS = "listIds";
	
	public String clientId;
	
	
	public static class AddInput implements Adapter<PushEntity> {
	  public String id;
	  
	  public AddInput() {}
	  
	  @Override
	  public PushEntity adapt() {
	    PushEntity pushEntity = new PushEntity();
	    pushEntity.clientId = id;
      return pushEntity;
	  }
	}

  @SuppressWarnings("serial")
  public static class Output extends ArrayList<PushEntity> {}

}

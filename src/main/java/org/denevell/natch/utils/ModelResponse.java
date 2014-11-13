package org.denevell.natch.utils;

import javax.ws.rs.core.Response;

public class ModelResponse<ReturnType> {
  
  public static interface ModelExternaliser {
    public Object toOutput();
  }
  
  public int code;
  public Object object;
  
  public ModelResponse(int code, Object o) {
    this.code = code;
    this.object = o;
  }

  public Response httpReturn() {
		if(this.code==404) {
		  return Response.status(404).build();
		}
		if(this.object instanceof ModelExternaliser) {
		  Object externalisd = ((ModelExternaliser)this.object).toOutput();
      return Response.ok().entity(externalisd).build();
		} else {
		  return Response.ok().entity(this.object).build();
		}
  }

}

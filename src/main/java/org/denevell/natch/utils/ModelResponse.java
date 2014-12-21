package org.denevell.natch.utils;

import javax.ws.rs.core.Response;

public class ModelResponse<ReturnType> {
  
  public static interface ModelExternaliser {
    public Object toOutput();
  }

  public static interface PushResourceExternaliser<T> {
    public Object toPushResource(T ob);
  }
  
  public int code;
  public ReturnType object;
  
  public ModelResponse(int code, ReturnType o) {
    this.code = code;
    this.object = o;
  }

  public Response httpReturn() {
    switch (this.code) {
    case 200:
      if (this.object != null && this.object instanceof ModelExternaliser) {
        Object externalisd = ((ModelExternaliser) this.object).toOutput();
        return Response.ok().entity(externalisd).build();
      } else {
        return Response.ok().entity(this.object).build();
      }
    default:
		  return Response.status(this.code).entity(this.object).build();
    }
  }

}

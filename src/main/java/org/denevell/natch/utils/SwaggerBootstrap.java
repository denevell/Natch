package org.denevell.natch.utils;

import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import javax.servlet.http.HttpServlet;

public class SwaggerBootstrap extends HttpServlet {
	static {
	    JaxrsApiReader.setFormatString("");
	  }
	private static final long serialVersionUID = 4835348439798476877L;
}	

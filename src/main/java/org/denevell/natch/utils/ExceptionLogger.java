package org.denevell.natch.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

public class ExceptionLogger implements Filter {
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(req, resp);		
		} catch (Exception e) {
			Logger.getLogger(getClass()).info("Exception while in REST project", e);
			throw e;
		}
	}

	@Override public void init(FilterConfig arg0) throws ServletException { } 
	@Override public void destroy() { }
}
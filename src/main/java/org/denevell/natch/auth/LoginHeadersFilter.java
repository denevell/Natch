package org.denevell.natch.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginHeadersFilter implements Filter {
	
	private static final String AUTHENTICATION_ID = "AuthId";
	public static final String KEY_SERVLET_REQUEST_LOGGEDIN_USERNAME= "auth";
	private LoginAuthKeysSingleton mAuthKeys;
	
	public LoginHeadersFilter() {
		mAuthKeys = LoginAuthKeysSingleton.getInstance();
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		// Get the request info for the header
		HttpServletRequest request = (HttpServletRequest) req;
		// Get the auth data from the header
		String authId = request.getHeader(AUTHENTICATION_ID);
		// Check it
		String username = mAuthKeys.retrieveUsername(authId);
		if(username==null || username.trim().length()==0) {
			HttpServletResponse r = (HttpServletResponse) resp;
			r.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Doesn't man you need to HTTP auth, but this is a good response fit.
			return;
		} else { // Since we're logged in, set the auth object
			request.setAttribute(KEY_SERVLET_REQUEST_LOGGEDIN_USERNAME, username);
			chain.doFilter(request, resp);		
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}

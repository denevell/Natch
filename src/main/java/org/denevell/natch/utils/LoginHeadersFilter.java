package org.denevell.natch.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.utils.UserGetLoggedInService.User;
import org.denevell.natch.utils.UserGetLoggedInService.UserGetLoggedInModelImpl;

public class LoginHeadersFilter implements Filter {
	
	private static final String AUTHENTICATION_KEY = "AuthKey";
	public static final String KEY_SERVLET_REQUEST_LOGGEDIN_USER= "authed_username";
	public static final String KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY = "authkey";
	private UserGetLoggedInService mModel = new UserGetLoggedInModelImpl();
	
	public LoginHeadersFilter() {
	}

	public LoginHeadersFilter(UserGetLoggedInService model) {
		mModel = model;
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		// Get the request info for the header
		HttpServletRequest request = (HttpServletRequest) req;
		// Get the auth data from the header
		String authKey = request.getHeader(AUTHENTICATION_KEY);
		// Check it
		User user = mModel.get(authKey);
		req.setAttribute("user", user);
		if(user==null || user.username==null || user.username.trim().length()==0) {
			HttpServletResponse r = (HttpServletResponse) resp;
			r.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Doesn't man you need to HTTP auth, but this is a good response fit.
			return;
		} else { // Since we're logged in, set the auth object
			request.setAttribute(KEY_SERVLET_REQUEST_LOGGEDIN_USER, user.username);
			request.setAttribute(KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY, authKey);
			chain.doFilter(request, resp);		
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	} 
}
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

import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.serv.users.UsersModel;
import org.denevell.natch.utils.Log;

public class LoginHeadersFilter implements Filter {
	
	private static final String AUTHENTICATION_KEY = "AuthKey";
	public static final String KEY_SERVLET_REQUEST_LOGGEDIN_USER= "authed_username";
	public static final String KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY = "authkey";
	private UsersModel mLoginModel;
	
	public LoginHeadersFilter() {
		mLoginModel = new UsersModel();
	}
	
	/**
	 * For testing with DI
	 */
	public LoginHeadersFilter(UsersModel model) {
		mLoginModel = model;
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
		UserEntity username = mLoginModel.loggedInAs(authKey);
		if(username==null || username.getUsername()==null || username.getUsername().trim().length()==0) {
			HttpServletResponse r = (HttpServletResponse) resp;
			r.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Doesn't man you need to HTTP auth, but this is a good response fit.
			return;
		} else { // Since we're logged in, set the auth object
			request.setAttribute(KEY_SERVLET_REQUEST_LOGGEDIN_USER, username);
			request.setAttribute(KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY, authKey);
			chain.doFilter(request, resp);		
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
	
	public static UserEntity getLoggedInUser(ServletRequest request) {
		try {
			UserEntity userEntity = (UserEntity) request.getAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USER);
			return userEntity;
		} catch (Exception e) {
			Log.info(LoginHeadersFilter.class, "Unable to get logged in user: " + e.toString());
			return null;
		}			
	}

}

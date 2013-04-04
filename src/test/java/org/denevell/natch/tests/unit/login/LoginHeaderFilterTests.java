package org.denevell.natch.tests.unit.login;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.denevell.natch.auth.LoginHeadersFilter;
import org.denevell.natch.serv.login.LoginModel;
import org.junit.Before;
import org.junit.Test;

public class LoginHeaderFilterTests {
	

	private LoginHeadersFilter filter;
	private LoginModel model;
	private HttpServletResponse resp;
	private HttpServletRequest req;
	private FilterChain chain;

	@Before
	public void setup() {
		model = mock(LoginModel.class);
		filter = new LoginHeadersFilter(model);
		chain = mock(FilterChain.class);
		req = mock(HttpServletRequest.class);
		resp = mock(HttpServletResponse.class);
	}
	
	@Test
	public void shouldAllowLogin() throws IOException, ServletException {
		// Arrange
		when(req.getHeader("AuthKey")).thenReturn("authKey");		
		when(model.loggedInAs("authKey")).thenReturn("username");
		
		// Act
		filter.doFilter(req, resp, chain);
		
		// Assert
		verify(req).getHeader("AuthKey");
		verify(req).setAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_USERNAME, "username");
		verify(req).setAttribute(LoginHeadersFilter.KEY_SERVLET_REQUEST_LOGGEDIN_AUTHKEY, "authKey");
		verify(chain).doFilter(req, resp);
	}	
	
	@Test
	public void shouldntAllowLogin() throws IOException, ServletException {
		// Arrange
		when(req.getHeader("AuthKey")).thenReturn("authKey");		
		when(model.loggedInAs("authKey")).thenReturn(null);
		
		// Act
		filter.doFilter(req, resp, chain);
		
		// Assert
		verify(req).getHeader("AuthKey");
		verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(chain, never()).doFilter(req, resp);
	}	
	
}

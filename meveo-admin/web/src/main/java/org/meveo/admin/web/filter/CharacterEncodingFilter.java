package org.meveo.admin.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter(filterName = "characterEncodingFilter", urlPatterns = { "/*" })
public class CharacterEncodingFilter implements Filter {
	private static final String ENCODING="UTF-8";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request.getCharacterEncoding() == null) {
			request.setCharacterEncoding(ENCODING);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}

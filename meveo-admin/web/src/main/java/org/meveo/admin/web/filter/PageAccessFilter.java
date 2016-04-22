package org.meveo.admin.web.filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.filter.config.Page;
import org.meveo.admin.web.filter.config.PageAccess;
import org.meveo.admin.web.filter.config.Param;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;

@WebFilter(filterName = "pageAccessFilter", urlPatterns = { "/pages/*" }, initParams = {
		@WebInitParam(name = "configFile", value = "/WEB-INF/page-access.xml"),
		@WebInitParam(name = "pagesDirectory", value = "/pages"),
		@WebInitParam(name = "errorPage", value = "/errors/403.jsf") })
public class PageAccessFilter implements Filter {

	private static final String CONFIG_FILE = "configFile";
	private static final String PAGES_DIRECTORY = "pagesDirectory";
	private static final String ERROR_PAGE = "errorPage";
	private FilterConfig config;
	private Map<String, Page> pages;

	@Inject
	private Logger logger;

	@Inject
	private Identity identity;

	@Override
	public void init(FilterConfig config) throws ServletException {

		// initialize config and pages properties
		this.config = config;
		this.pages = Collections.synchronizedMap(new HashMap<String, Page>());

		// load page configuration from page-access.xml file
		String pageAccessFileName = config.getServletContext().getRealPath(config.getInitParameter(CONFIG_FILE));
		File pageAccessFile = new File(pageAccessFileName);
		try {
			JAXBContext context = JAXBContext.newInstance(PageAccess.class);
			PageAccess pageAccess = (PageAccess) context.createUnmarshaller().unmarshal(pageAccessFile);
			if (pageAccess != null) {
				StringBuilder key = new StringBuilder();
				for (Page page : pageAccess.getPages()) {
					// key is a concatenation of the contextPath, the
					// pageDirectory, and the view-id
					key.setLength(0);
					key.append(config.getServletContext().getContextPath());
					key.append(config.getInitParameter(PAGES_DIRECTORY));
					key.append(page.getViewId());
					this.pages.put(key.toString(), page);
				}
			}
		} catch (JAXBException e) {
			throw new ServletException(e);
		}

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String pageKey = request.getRequestURI();
		if(pageKey!=null && pageKey.endsWith(".jsf")){
			pageKey = pageKey.substring(0, pageKey.lastIndexOf(".jsf"));
		} else if(pageKey!=null && pageKey.endsWith(".xhtml")){
			pageKey = pageKey.substring(0, pageKey.lastIndexOf(".xhtml"));
		}

		Page page = this.pages.get(pageKey);
		
		if (identity != null && identity.isLoggedIn() && page != null) {
			
			List<Object> parameters = new ArrayList<>();

			// load the identity object as the currentUser
			parameters.add("currentUser");
			parameters.add(this.identity);

			String value = null;
			String key = null;

			// load all parameters needed for the expression's evaluation
			for (Param paramKey : page.getParameters()) {
				key = paramKey.getName();
				value = request.getParameter(key);
				if (value != null) {
					parameters.add(key);
					parameters.add(value);
				}
			}

			boolean allow = true;
			// load all constraints
			for (String constraint : page.getConstraints()) {
				try {
					allow = allow
							&& ValueExpressionWrapper.evaluateToBooleanMultiVariable(constraint, parameters.toArray());
					// if allow is false any succeeding expressions will never be true.
					// so immediately log a warning then redirect user to error page.
					if (!allow) {
						logger.warn("User does not have permission to access the page. Redirecting to error page...");
						response.sendRedirect(
								request.getServletContext().getContextPath() + config.getInitParameter(ERROR_PAGE));
						return;
					}
				} catch (BusinessException e) {
					logger.error("Failed to execute constraint expression. Redirecting to error page...", e);
					response.sendRedirect(
							request.getServletContext().getContextPath() + config.getInitParameter(ERROR_PAGE));
					return;
				}
			}
		}
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {
		// do nothing
	}

}

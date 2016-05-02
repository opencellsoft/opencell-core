package org.meveo.admin.web.filter;

import java.io.File;
import java.io.IOException;

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
import org.meveo.admin.web.filter.config.Page;
import org.meveo.admin.web.filter.config.PageAccess;
import org.meveo.util.view.PagePermission;

@WebFilter(filterName = "pageAccessFilter", urlPatterns = { "/pages/*" }, initParams = {
		@WebInitParam(name = "configFile", value = "/WEB-INF/page-access.xml"),
		@WebInitParam(name = "pagesDirectory", value = "/pages"),
		@WebInitParam(name = "errorPage", value = "/errors/403.jsf") })
public class PageAccessFilter implements Filter {

	private static final String CONFIG_FILE = "configFile";
	private static final String ERROR_PAGE = "errorPage";
	private static final String PAGES_DIRECTORY = "pagesDirectory";
	
	private FilterConfig config;
	
	@Inject
	private Identity identity;

	@Override
	public void destroy() {
		// do nothing
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		Page page = PagePermission.getInstance().findPage(request.getRequestURI());
		
		if (identity != null && identity.isLoggedIn() && page != null) {
			boolean allowed = PagePermission.getInstance().hasAccessToPage(request, identity);
			if(!allowed){
				response.sendRedirect(request.getServletContext().getContextPath() + config.getInitParameter(ERROR_PAGE));
			}
		}
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

		// initialize config and pages properties
		this.config = config;
		
		// load page configuration from page-access.xml file
		String pageAccessFileName = config.getServletContext().getRealPath(config.getInitParameter(CONFIG_FILE));
		File pageAccessFile = new File(pageAccessFileName);
		try {
			JAXBContext context = JAXBContext.newInstance(PageAccess.class);
			PageAccess pageAccess = (PageAccess) context.createUnmarshaller().unmarshal(pageAccessFile);
			if (pageAccess != null) {
				StringBuilder key = new StringBuilder();
				for (Page page : pageAccess.getPages()) {
					// key is a concatenation of the contextPath, the pageDirectory, and the view-id
					key.setLength(0);
					key.append(config.getServletContext().getContextPath());
					key.append(config.getInitParameter(PAGES_DIRECTORY));
					key.append(page.getViewId());
					PagePermission.getInstance().getPages().put(key.toString(), page);
				}
			}
		} catch (JAXBException e) {
			throw new ServletException("Unable to unmarshall page-access rules.", e);
		}

	}

}

package org.meveo.util.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.filter.config.ConstraintType;
import org.meveo.admin.web.filter.config.Page;
import org.meveo.admin.web.filter.config.Param;
import org.meveo.commons.utils.StringUtils;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagePermission {

	private static final String PAGE_ACCESS_RULES_EXCEPTION = "Page access rules have not been initialized. Call the init() method to initialize properly.";
	private static final String CURRENT_USER = "currentUser";
	private static final String JSF = ".jsf";
	private static final String XHTML = ".xhtml";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<Page> pages;
	private String pagesDirectory;

	// Private constructor. Prevents instantiation from other classes.
	private PagePermission() {
	}

	/**
	 * Initializes singleton.
	 * 
	 * {@link PagePermissionHolder} is loaded on the first execution of
	 * {@link PagePermission#getInstance()} or the first access to
	 * {@link PagePermissionHolder#INSTANCE}, not before.
	 */
	private static class PagePermissionHolder {
		private static final PagePermission INSTANCE = new PagePermission();
	}

	public static PagePermission getInstance() {
		return PagePermissionHolder.INSTANCE;
	}

	public void init(String pagesDirectory, List<Page> pages) {
		this.pages = pages;
		this.pagesDirectory = pagesDirectory;
	}

	public boolean isPageProtected(HttpServletRequest request) {
		boolean exists = true;
		String requestURI = request.getRequestURI();
		if (this.pages != null && requestURI != null && requestURI.startsWith(pagesDirectory)) {
			Page page = matchPage(request.getRequestURI());
			if (page == null) {
				exists = false;
			}
		}
		return exists;
	}

	public boolean hasAccessToPage(HttpServletRequest request, MeveoUser currentUser) throws BusinessException {
		boolean allow = checkConstraints(request, currentUser, ConstraintType.READ);
		return allow;
	}

	public boolean hasWriteAccess(HttpServletRequest request, MeveoUser currentUser) throws BusinessException {
		boolean allow = checkConstraints(request, currentUser, ConstraintType.WRITE);
		return allow;
	}

	private boolean checkConstraints(HttpServletRequest request, MeveoUser currentUser, ConstraintType type)
			throws BusinessException {
		boolean allow = false;
		if (this.pages == null) {
			logger.error(PAGE_ACCESS_RULES_EXCEPTION);
			throw new BusinessException(PAGE_ACCESS_RULES_EXCEPTION);
		}
		String requestURI = request.getRequestURI();
		if (requestURI != null && requestURI.startsWith(pagesDirectory)) {
			Page page = matchPage(requestURI);
			if (page == null) {
				return false;
			}

            if ((currentUser != null && page != null)) { // && currentUser.isLoggedIn()
                Map<Object, Object> parameters = fetchParameters(page, request, currentUser);
                allow = ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(page.getExpression(type), parameters);
            }
		} else {
			allow = true;
		}

		return allow;
	}

	private Map<Object, Object> fetchParameters(Page page, HttpServletRequest request, MeveoUser currentUser) {

		Map<Object, Object> parameters = new HashMap<>();

		if (page != null && page.getParameters() != null && !page.getParameters().isEmpty()) {
			String value = null;
			String key = null;

			// load all parameters needed for the expression's evaluation
			for (Param paramKey : page.getParameters()) {
				key = paramKey.getName();
				value = request.getParameter(key);
				if (value != null) {
					parameters.put(key, value);
				}
			}
		}
		// load the identity object as the currentUser
		parameters.put(CURRENT_USER, currentUser);

		return parameters;
	}

	/**
	 * Match as close as possible a page access rule key to the page URI
	 * provided and return the page. Match is performed by regular expression.
	 * 
	 * @param requestUri
	 *            The page URI to match.
	 * @return The {@link Page} object of the matching page access rule.
	 */
	private Page matchPage(String requestUri) {
		String uri = null;
		if (requestUri != null && requestUri.endsWith(JSF)) {
			uri = requestUri.substring(0, requestUri.lastIndexOf(JSF));
		} else if (requestUri != null && requestUri.endsWith(XHTML)) {
			uri = requestUri.substring(0, requestUri.lastIndexOf(XHTML));
		}
		if (!StringUtils.isBlank(uri)) {
			Matcher matcher = null;
			for (Page page : this.pages) {
				matcher = page.getPattern().matcher(uri);
				if (matcher.matches()) {
					return page;
				}
			}
		}
		return null;
	}

}

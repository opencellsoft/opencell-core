package org.meveo.util.view;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.filter.config.Page;
import org.meveo.admin.web.filter.config.Param;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagePermission {

	private static final String PAGE_ACCESS_RULES_EXCEPTION = "Page access rules have not been initialized. Call the init() method to initialize properly.";
	private static final String CURRENT_USER = "currentUser";
	private static final String EDIT = "edit";
//	private static final String GET = "GET";
	private static final String JSF = ".jsf";
	private static final String XHTML = ".xhtml";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, Page> pages;
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

	public void init(String pagesDirectory, Map<String, Page> pages) {
		this.pages = pages;
		this.pagesDirectory = pagesDirectory;
	}

	public boolean isPageExisting(HttpServletRequest request) {
		boolean exists = true;
		String requestURI = request.getRequestURI();
		if (this.pages != null && requestURI != null && requestURI.startsWith(pagesDirectory)) {
			String key = findMatchingKey(request.getRequestURI());
			if (StringUtils.isEmpty(key)) {
				exists = false;
			}
		}
		return exists;
	}

	public boolean hasAccessToPage(HttpServletRequest request, Identity currentUser) throws BusinessException {
		// determine edit mode using request method.
		//boolean edit = false;//!GET.equalsIgnoreCase(request.getMethod());
		boolean allow = checkConstraints(request, currentUser, false);
		return allow;
	}

	public boolean hasWriteAccess(HttpServletRequest request, Identity currentUser) throws BusinessException {
		// for checking write access, edit will always be true.
		//boolean edit = true;
		boolean allow = checkConstraints(request, currentUser, true);
		return allow;
	}

	private boolean checkConstraints(HttpServletRequest request, Identity currentUser, boolean edit)
			throws BusinessException {
		boolean allow = false;
		if (this.pages == null) {
			logger.error(PAGE_ACCESS_RULES_EXCEPTION);
			throw new BusinessException(PAGE_ACCESS_RULES_EXCEPTION);
		}
		String requestURI = request.getRequestURI();
		if (requestURI != null && requestURI.startsWith(pagesDirectory)) {
			String key = findMatchingKey(requestURI);
			if (!StringUtils.isEmpty(key)) {
				Page page = this.pages.get(key);
				if ((currentUser != null && currentUser.isLoggedIn() && page != null)) {
					Map<Object, Object> parameters = fetchParameters(page, request, currentUser, edit);
					// load all constraints
					allow = true;
					for (String constraint : page.getConstraints()) {
						try {
							allow = allow && (Boolean) ValueExpressionWrapper.evaluateExpression(constraint, parameters,
									Boolean.class);
							// if allow is false any succeeding expressions will
							// never be true so immediately log then break from
							// loop.
							if (!allow) {
								logger.debug("User does not have permission. Returning false.");
								break;
							}
						} catch (BusinessException e) {
							logger.error("Failed to execute constraint expression. Returning false.", e);
							allow = false;
							break;
						}
					}
				}
			}
		} else {
			allow = true;
		}

		return allow;
	}

	private Map<Object, Object> fetchParameters(Page page, HttpServletRequest request, Identity currentUser,
			boolean edit) {

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

		// load the edit parameter.
		parameters.put(EDIT, edit);

		return parameters;
	}

	private String trimPageExtension(String url) {
		if (url != null && url.endsWith(JSF)) {
			url = url.substring(0, url.lastIndexOf(JSF));
		} else if (url != null && url.endsWith(XHTML)) {
			url = url.substring(0, url.lastIndexOf(XHTML));
		}
		return url;
	}

	/**
	 * Match as close as possible a page access rule key to the page URI
	 * provided and return a the page access rule key. Match is performed by
	 * matching a full string and then reducing one by one symbol until a match
	 * is found.
	 * 
	 * @param requestUri
	 *            The page URI to match.
	 * @return The key of the matching page access rule.
	 */
	private String findMatchingKey(String requestUri) {
		if (this.pages == null || !(this.pages instanceof Map) || StringUtils.isEmpty(requestUri)) {
			return null;
		}
		String uri = trimPageExtension(requestUri);
		Page page = null;
		StringBuilder key = new StringBuilder();
		for (int i = uri.length(); i > 0; i--) {
			key.append(uri.substring(0, i));
			page = this.pages.get(key.toString());
			if (page != null) {
				break;
			} else {
				key.append("*");
				page = this.pages.get(key.toString());
				if (page != null) {
					break;
				} else {
					key.setLength(0);
				}
			}
		}
		return key.toString();
	}

}

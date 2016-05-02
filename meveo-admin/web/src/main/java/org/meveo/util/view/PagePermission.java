package org.meveo.util.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.filter.config.Page;
import org.meveo.admin.web.filter.config.Param;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagePermission {

	private static final String CURRENT_USER = "currentUser";
	private static final String EDIT = "edit";
	private static final String GET = "GET";
	private static final String JSF = ".jsf";
	private static final String XHTML = ".xhtml";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, Page> pages;

	// Private constructor. Prevents instantiation from other classes.
	private PagePermission() {
		pages = Collections.synchronizedMap(new HashMap<String, Page>());
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

	public Map<String, Page> getPages() {
		return pages;
	}

	public Page findPage(String url) {
		url = trimPageExtension(url);
		return this.pages.get(url);
	}

	public boolean hasAccessToPage(HttpServletRequest request, Identity currentUser) {

		Page page = findPage(request.getRequestURI());
		Map<Object, Object> parameters = new HashMap<>();

		// load the identity object as the currentUser
		parameters.put(CURRENT_USER, currentUser);

		// load the edit parameter automatically
		// boolean edit = Boolean.getBoolean(request.getParameter("edit"));
		boolean edit = !GET.equalsIgnoreCase(request.getMethod());
		parameters.put(EDIT, edit);

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

		boolean allow = true;
		// load all constraints
		for (String constraint : page.getConstraints()) {
			try {
				allow = allow && (Boolean) ValueExpressionWrapper.evaluateExpression(constraint, parameters, Boolean.class);
				// if allow is false any succeeding expressions will never be true.
				// so immediately log a warning then break from loop.
				if (!allow) {
					logger.warn("User does not have permission to access the page. Returning false...");
					break;
				}
			} catch (BusinessException e) {
				logger.error("Failed to execute constraint expression. Returning false...", e);
				allow = false;
				break;
			}
		}
		return allow;
	}

	public boolean hasWriteAccess(HttpServletRequest request, User currentUser) {
		// define exemptions, if user is admin or superadmin or if the request
		// is not an action, return true
		boolean isAdmin = currentUser.hasPermission("administration", "administrationManagement");
		boolean isSuperAdmin = currentUser.hasPermission("superAdmin", "superAdminManagement");
//		boolean isEdit = !GET.equalsIgnoreCase(request.getMethod());

		boolean hasWriteAccess = false;
		if (isAdmin || isSuperAdmin) {
			hasWriteAccess = true;
		} else {
			// check if user has permissions to manage the resource
			List<String> resources = getResourcesFromUrl(request.getRequestURI());
			
			if(resources != null){
				for (String resource : resources) {
					if (currentUser.hasPermission(resource, resource + "Management")) {
						hasWriteAccess = true;
						break;
					}
				}
			}
		}
		return hasWriteAccess;
	}

	private List<String> getResourcesFromUrl(String path) {
		String categoryAndEntity = StringUtils.patternMacher("/pages/(.*/.*)/", path);
		List<String> resources = null;
		if (categoryAndEntity != null && categoryAndEntity.contains("/")) {
			resources = Arrays.asList(categoryAndEntity.split("/"));
		}
		return resources;
	}

	private String trimPageExtension(String url) {
		if (url != null && url.endsWith(JSF)) {
			url = url.substring(0, url.lastIndexOf(JSF));
		} else if (url != null && url.endsWith(XHTML)) {
			url = url.substring(0, url.lastIndexOf(XHTML));
		}
		return url;
	}

}

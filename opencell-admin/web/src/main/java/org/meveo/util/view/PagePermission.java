/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.util.view;

import java.lang.invoke.MethodHandles;
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

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
			log.error(PAGE_ACCESS_RULES_EXCEPTION);
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

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

package org.meveo.admin.web.listener;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.meveo.admin.exception.BusinessException;
import org.meveo.security.MeveoUser;
import org.meveo.util.view.PagePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class PageAccessListener implements PhaseListener {

    private static final long serialVersionUID = 6092965175755558330L;
    private static final String FORBIDDEN = "forbidden";
    private static final String NOT_FOUND = "notFound";
    private static final String CURRENT_USER = "#{currentUser}";

    private static final Logger logger = LoggerFactory.getLogger(PageAccessListener.class);

    @Override
    public void afterPhase(PhaseEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        NavigationHandler navigationHandler = null;
        String requestURI = request.getRequestURI();

        boolean isPageProtected = PagePermission.getInstance().isPageProtected(request);
        if (!isPageProtected) {

            logger.error("Access to page {} has no rules defined and will be rejected", requestURI);
            navigationHandler = context.getApplication().getNavigationHandler();
            navigationHandler.handleNavigation(context, null, NOT_FOUND);
            return;
        }

        boolean allowed = false;
        try {
            MeveoUser currentUser = context.getApplication().evaluateExpressionGet(context, CURRENT_USER, MeveoUser.class);
            if (currentUser != null && currentUser.isAuthenticated()) {

                if (currentUser.getProviderCode() == null) {
                    MDC.remove("providerCode");
                } else {
                    MDC.put("providerCode", currentUser.getProviderCode());
                }

                logger.trace("Checking access to page: {}", requestURI);
                allowed = PagePermission.getInstance().hasAccessToPage(request, currentUser);
            } else {
                // if user is not logged in, allow session handler to redirect to session expired page.
                return;
            }
        } catch (BusinessException e) {
            logger.error("Failed to check access to page: {}. Access will be denied.", requestURI, e);
            allowed = false;
        }
        if (!allowed) {
            logger.warn("Denied access to page: {}", requestURI);
            navigationHandler = context.getApplication().getNavigationHandler();
            navigationHandler.handleNavigation(context, null, FORBIDDEN);
            return;
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        // do nothing
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

}

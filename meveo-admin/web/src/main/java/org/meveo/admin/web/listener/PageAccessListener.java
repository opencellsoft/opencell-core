package org.meveo.admin.web.listener;

import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.BusinessException;
import org.meveo.util.view.PagePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageAccessListener implements PhaseListener {

	private static final long serialVersionUID = 6092965175755558330L;
	private static final String FORBIDDEN = "forbidden";
	private static final String NOT_FOUND = "notFound";
	private static final String LOGOUT_ACTION = "#{identity.logout}";
	private static final String IDENTITY = "#{identity}";

	private static final Logger logger = LoggerFactory.getLogger(PageAccessListener.class);

	@Override
	public void afterPhase(PhaseEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		NavigationHandler navigationHandler = null;
		String requestURI = request.getRequestURI();
		logger.trace("Checking access to page: {}", requestURI);

		if (!isLogoutAction(request)) {
			boolean pageExists = PagePermission.getInstance().isPageExisting(request);
			if (!pageExists) {
				navigationHandler = context.getApplication().getNavigationHandler();
				navigationHandler.handleNavigation(context, null, NOT_FOUND);
			}

			boolean allowed = false;
			try {
				Identity identity = context.getApplication().evaluateExpressionGet(context, IDENTITY, Identity.class);
				allowed = PagePermission.getInstance().hasAccessToPage(request, identity);
			} catch (BusinessException e) {
				logger.error("Failed to check access to page: {}", requestURI, e);
				allowed = false;
			}
			if (!allowed) {
				navigationHandler = context.getApplication().getNavigationHandler();
				navigationHandler.handleNavigation(context, null, FORBIDDEN);
			}
			logger.trace("Allow access to page: {} is {}", requestURI, allowed);
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

	private boolean isLogoutAction(HttpServletRequest request) {
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot view = context.getViewRoot();
		boolean logout = false;
		if (view != null) {
			Map<String, String> params = context.getExternalContext().getRequestParameterMap();
			UICommand command = null;

			UIComponent component = null;
			for (String clientId : params.keySet()) {
				component = view.findComponent(clientId);
				if (component instanceof UICommand) {
					command = (UICommand) component;
				}
			}
			if (command != null) {
				MethodExpression actionExpression = command.getActionExpression();
				if (actionExpression != null) {
					String action = actionExpression.getExpressionString();
					logout = LOGOUT_ACTION.equals(action);
				}
			}
		}
		return logout;
	}

}

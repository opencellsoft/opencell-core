package org.meveo.util.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.meveo.security.keycloak.CurrentUserProvider;

/**
 * Page/url access security check based on Keycloak authorization URL rules
 * 
 * @author Andrius Karpavicius
 */
@Named
@SessionScoped
public class PageAccessHandler implements Serializable {

    private static final long serialVersionUID = 8756738833715989536L;

    @Inject
    private HttpServletRequest httpRequest;

    @Inject
    private CurrentUserProvider currentUserProvider;

    private Map<String, Boolean> pageAccess;

    @PostConstruct
    public void init() {
        pageAccess = new HashMap<String, Boolean>();
    }

    /**
     * Check if outcome, as defined in faces-config.xml file, in a given scope is allowed for a current user. </br>
     * A check in Keyckloak authorization rules is done for a URL that outcome is resolved to plus a given scope.
     * 
     * @param scope A scope to match. Optional. Use GET for read and POST for update.
     * @param outcomes A list of outcomes to check.
     * @return True if <b>any</b> of the outcomes is accessible
     */
    public boolean isOutcomeAccesible(String scope, String... outcomes) {

        FacesContext context = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler navigationHandler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();

        for (String outcome : outcomes) {

            Boolean isAccessible = pageAccess.get(outcome + "-" + scope);
            if (isAccessible == null) {

                NavigationCase navCase = navigationHandler.getNavigationCase(context, null, outcome);
                if (navCase == null) {
                    return true;
                }
                String targetUrl = navCase.getToViewId(context);

                isAccessible = currentUserProvider.isLinkAccesible(targetUrl, scope);
                pageAccess.put(outcome + "-" + scope, isAccessible);
            }

            if (isAccessible) {
                return isAccessible;
            }
        }
        return false;
    }

    /**
     * Check if URL with a given scope is allowed for a current user in Keyckloak authorization rules.
     * 
     * @param scope A scope to match. Optional. Use GET for read and POST for update
     * @param url A URL to check
     * @return True if it is accessible
     */
    public boolean isURLAccesible(String scope, String url) {

        Boolean isAccessible = pageAccess.get(url + "-" + scope);
        if (isAccessible == null) {

            isAccessible = currentUserProvider.isLinkAccesible(url, scope);
            pageAccess.put(url + "-" + scope, isAccessible);
        }

        if (isAccessible) {
            return isAccessible;
        }
        return false;
    }

    /**
     * Check if current request URL with a given scope is allowed for a current user in Keyckloak authorization rules.
     * 
     * @param scope A scope to match. Optional. Use GET for read and POST for update
     * @return True if it is accessible
     */
    public boolean isCurrentURLAccesible(String scope) {

        String requestURI = httpRequest.getRequestURI();
        requestURI = requestURI.substring(requestURI.substring(1).indexOf("/") + 1);

        return isURLAccesible(scope, requestURI);
    }
}
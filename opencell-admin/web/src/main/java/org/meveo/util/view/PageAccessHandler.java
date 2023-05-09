package org.meveo.util.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.meveo.cache.AuthorizationCacheContainerProvider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;

/**
 * Page/url access security check based on Keycloak authorization URL rules
 * 
 * @author Andrius Karpavicius
 */
@Named
@RequestScoped
public class PageAccessHandler implements Serializable {

    private static final long serialVersionUID = 8756738833715989536L;

    @Inject
    private HttpServletRequest httpRequest;

    @Inject
    private AuthorizationCacheContainerProvider authorizationCache;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * Check if outcome, as defined in faces-config.xml file, in a given scope is allowed for a current user. </br>
     * A check in Keycloak authorization rules is done for a URL that outcome is resolved to plus a given scope.
     * 
     * @param scope A scope to match. Optional. Use GET for read and POST for update.
     * @param outcomes A list of outcomes to check.
     * @return True if <b>any</b> of the outcomes is accessible
     */
    public boolean isOutcomeAccesible(String scope, String... outcomes) {

        FacesContext context = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler navigationHandler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();

        boolean finalIsAccessible = false;

        List<String> outcomesToCheck = new ArrayList<String>();
        List<String> urlsToCheck = new ArrayList<String>();

        // Checked cached outcome results and gather a list of outcomes/urls to check
        for (String outcome : outcomes) {

            Boolean isAccessible = authorizationCache.isAuthorized(currentUser.getAuthenticationTokenId(), outcome + "-" + scope);
            if (isAccessible == null) {

                NavigationCase navCase = navigationHandler.getNavigationCase(context, null, outcome);
                if (navCase == null) {
                    continue;
                }

                // Replace .xhtml with .jsf and remove any url parameters
                String targetUrl = navCase.getToViewId(context);
                targetUrl = targetUrl.substring(0, targetUrl.indexOf(".xhtml")) + ".jsf";

                outcomesToCheck.add(outcome);
                urlsToCheck.add(targetUrl);

            } else if (isAccessible) {
                finalIsAccessible = true;
            }
        }

        // Check outcomes/URLs that were not cached before
        if (!urlsToCheck.isEmpty()) {
            boolean[] urlAccessible = CurrentUserProvider.isLinkAccesible((HttpServletRequest) context.getExternalContext().getRequest(), scope, urlsToCheck.toArray(new String[urlsToCheck.size()]));

            Map<String, Boolean> checkResults = new HashMap<String, Boolean>(outcomesToCheck.size() * 2);

            for (int i = 0; i < outcomesToCheck.size(); i++) {
                boolean isUrlAccesible = urlAccessible[i];

                checkResults.put(outcomesToCheck.get(i) + "-" + scope, isUrlAccesible);
                checkResults.put(urlsToCheck.get(i) + "-" + scope, isUrlAccesible);

                if (isUrlAccesible) {
                    finalIsAccessible = true;
                }
            }
            authorizationCache.addAuthorization(currentUser.getAuthenticationTokenId(), checkResults);
        }

        return finalIsAccessible;
    }

    /**
     * Check if URL with a given scope is allowed for a current user in Keycloak authorization rules.
     * 
     * @param scope A scope to match. Optional. Use GET for read and POST for update
     * @param url A URL to check
     * @return True if it is accessible
     */
    public boolean isURLAccesible(String scope, String url) {

        // Omit checking access for JSF resources
        if (url.startsWith("/javax.faces.resource/")) {
            return true;
        }

        Boolean isAccessible = authorizationCache.isAuthorized(currentUser.getAuthenticationTokenId(), url + "-" + scope);
        if (isAccessible == null) {

            boolean[] isAccessibles = CurrentUserProvider.isLinkAccesible(httpRequest, scope, url);
            isAccessible = isAccessibles[0];
            authorizationCache.addAuthorization(currentUser.getAuthenticationTokenId(), url + "-" + scope, isAccessible);
        }

        if (isAccessible) {
            return isAccessible;
        }
        return false;
    }

    /**
     * Check if current request URL with a given scope is allowed for a current user in Keycloak authorization rules.
     * 
     * @param scope A scope to match. Optional. Use GET for read and POST for update
     * @return True if it is accessible
     */
    public boolean isCurrentURLAccesible(String scope) {

        String requestURI = httpRequest.getRequestURI();
        requestURI = requestURI.substring(requestURI.substring(1).indexOf("/") + 1);

        return isURLAccesible(scope, requestURI);
    }

    /**
     * Check if current request URL with a current scope (HTTP method) is allowed for a current user in Keycloak authorization rules.
     * 
     * @return True if it is accessible
     */
    public boolean isCurrentURLAccesible() {
        return isCurrentURLAccesible(httpRequest.getMethod());
    }
}
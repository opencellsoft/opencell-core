package org.meveo.security.keycloak;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.keycloak.KeycloakPrincipal;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple CDI service which is able to say hello to someone
 *
 * @author Pete Muir
 *
 */
@Stateless
public class CurrentUserProvider {

    @Resource
    private SessionContext ctx;

    // @Inject
    // @MeveoRequest
    // private ServletRequestWrapper httpRequestWrapper;

    // @Inject
    // private HttpServletRequest httpRequest;

    private String forcedUserSubject;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void forceAuthentication(String currentUserSubject) {

        // Current user is already authenticated, can't overwrite it
        if (ctx.getCallerPrincipal() instanceof KeycloakPrincipal || this.forcedUserSubject != null) {
            log.debug("Current user is already authenticated, can't overwrite it keycloak: {}", ctx.getCallerPrincipal() instanceof KeycloakPrincipal);
            return;
        }
        this.forcedUserSubject = currentUserSubject;
    }

    @Produces
    @RequestScoped
    @Named("currentUser")
    @CurrentUser
    public MeveoUser getCurrentUser() {

        MeveoUser user = null;

        // User was forced authenticated, so need to lookup the rest of user information
        if (!(ctx.getCallerPrincipal() instanceof KeycloakPrincipal) && forcedUserSubject != null) {
            user = new MeveoUserKeyCloakImpl(ctx, forcedUserSubject, forcedUserSubject, null, null);

        } else {
            user = new MeveoUserKeyCloakImpl(ctx, null, null, null, null);
        }
        log.debug("Produced {}", user);
        return user;
    }

}

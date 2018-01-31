package org.meveo.security.keycloak;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.keycloak.KeycloakPrincipal;
import org.meveo.model.admin.User;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.UserAuthTimeProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class CurrentUserProvider {

    private static Map<String, Set<String>> roleToPermissionMapping;

    @Resource
    private SessionContext ctx;
    
    @Inject
    private Instance<UserAuthTimeProducer> userAuthTimeProducer;

    private String forcedUserUsername;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void forceAuthentication(String currentUserUserName) {

        // Current user is already authenticated, can't overwrite it
        if (ctx.getCallerPrincipal() instanceof KeycloakPrincipal || this.forcedUserUsername != null) {
            log.debug("Current user is already authenticated, can't overwrite it keycloak: {}", ctx.getCallerPrincipal() instanceof KeycloakPrincipal);
            return;
        }
        this.forcedUserUsername = currentUserUserName;
    }
    
    public String getCurrentUserProviderCode() {
    	return MeveoUserKeyCloakImpl.extractProviderCode(ctx);
    	
    	
    }

    /**
     * return a current user from JAAS security context
     * 
     * @return Current user implementation
     */
    public MeveoUser getCurrentUser(EntityManager em) {

        String username = MeveoUserKeyCloakImpl.extractUsername(ctx, forcedUserUsername);

        MeveoUser user = null;

        // User was forced authenticated, so need to lookup the rest of user information
        if (!(ctx.getCallerPrincipal() instanceof KeycloakPrincipal) && forcedUserUsername != null) {
            user = new MeveoUserKeyCloakImpl(ctx, forcedUserUsername, getAdditionalRoles(username,em), getRoleToPermissionMapping(em));

        } else {
            user = new MeveoUserKeyCloakImpl(ctx, null, getAdditionalRoles(username,em), getRoleToPermissionMapping(em));
        }
        log.info("getCurrentUser username={},em={},providerCode={}",username,em,user!=null?user.getProviderCode():null);
        supplementOrCreateUserInApp(user,em);

        log.trace("Current user is {}", user);
        return user;
    }

    /**
     * Register a user in application if accesing for the first time with that username
     * 
     * @param currentUser Authenticated current user
     */
    private void supplementOrCreateUserInApp(MeveoUser currentUser,EntityManager em) {

        // Takes care of anonymous users
        if (currentUser.getUserName() == null) {
            return;
        }

        // Create or retrieve current user
        try {
            User user = null;
            try {
                user = em.createNamedQuery("User.getByUsername", User.class).setParameter("username", currentUser.getUserName().toLowerCase()).getSingleResult();

                if (userAuthTimeProducer.get().getAuthTime() != currentUser.getAuthTime()) {
                    userAuthTimeProducer.get().setAuthTime(currentUser.getAuthTime());
                    user.setLastLoginDate(new Date());
                    em.merge(user);
                    em.flush();
                }

            } catch (NoResultException e) {

                user = new User();
                user.setUserName(currentUser.getUserName().toUpperCase());
                if (currentUser.getFullName() != null) {
                    if (user.getName() == null) {
                        user.setName(new Name());
                    }
                    int spacePos = currentUser.getFullName().indexOf(' ');
                    if (spacePos > 0) {
                        user.getName().setFirstName(currentUser.getFullName().substring(0, spacePos));
                        user.getName().setLastName(currentUser.getFullName().substring(spacePos + 1));
                    } else {
                        user.getName().setFirstName(currentUser.getFullName());
                    }
                }
                user.setLastLoginDate(new Date());
                user.updateAudit(currentUser);
                em.persist(user);
                em.flush();
                log.info("A new application user was registered with username {} and name {}", user.getUserName(), user.getName().getFullName());
         
            } catch (ContextNotActiveException e) {
                log.error("No session context={}", e.getMessage());
            }

        } catch (Exception e) {
            log.error("Failed to supplement current user information from db and/or create new user in db", e);
        }

    }

    /**
     * Return and load if necessary a mapping between roles and permissions
     * 
     * @return A mapping between roles and permissions
     */
    private Map<String, Set<String>> getRoleToPermissionMapping(EntityManager em) {

        synchronized (this) {
            if (CurrentUserProvider.roleToPermissionMapping == null) {
                CurrentUserProvider.roleToPermissionMapping = new HashMap<>();

                try {
                    List<Role> userRoles = em.createNamedQuery("Role.getAllRoles", Role.class).getResultList();

                    for (Role role : userRoles) {
                        Set<String> rolePermissions = new HashSet<>();
                        for (Permission permission : role.getAllPermissions()) {
                            rolePermissions.add(permission.getPermission());
                        }

                        CurrentUserProvider.roleToPermissionMapping.put(role.getName(), rolePermissions);
                    }

                } catch (Exception e) {
                    log.error("Failed to construct role to permission mapping", e);
                }
            }

            return CurrentUserProvider.roleToPermissionMapping;
        }
    }

    /**
     * Invalidate cached role to permission mapping (usually after role save/update event)
     */
    public void invalidateRoleToPermissionMapping() {
        CurrentUserProvider.roleToPermissionMapping = null;
    }

    /**
     * Get additional roles that user has assigned in application
     * 
     * @param username Username to check
     * @return A set of role names that given username has in application
     */
    private Set<String> getAdditionalRoles(String username,EntityManager em) {

        // Takes care of anonymous users
        if (username == null) {
            return null;
        }

        try {
            User user = em.createNamedQuery("User.getByUsername", User.class).setParameter("username", username.toLowerCase()).getSingleResult();

            Set<String> additionalRoles = new HashSet<>();

            for (Role role : user.getRoles()) {
                additionalRoles.add(role.getName());
            }

            return additionalRoles;

        } catch (NoResultException e) {
            return null;

        } catch (Exception e) {
            log.error("Failed to retrieve additional roles for a user {}", username, e);
            return null;
        }
    }
}
package org.meveo.security.keycloak;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.model.admin.User;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.security.MeveoUser;
import org.meveo.security.UserAuthTimeProducer;
import org.slf4j.Logger;

@Stateless
public class UserInfoManagement {
    @Inject
    private Logger log;

    /**
     * Map<providerCode, Map<roleName, rolePermissions>>
     */
    private static Map<String, Map<String, Set<String>>> roleToPermissionMapping = new HashMap<>();

    @Inject
    private Event<User> userEventProducer;

    @Inject
    private Instance<UserAuthTimeProducer> userAuthTimeProducer;

    /**
     * Update user's last login date in db
     * 
     * @param currentUser Currently logged in user
     * @param em Entity manager
     * @param forcedUsername
     * @return False if user does not exist yet. True if user was updated or does not apply (anonymous users, forced users)
     */
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean supplementUserInApp(MeveoUser currentUser, EntityManager em, String forcedUsername) {

        // Takes care of anonymous or forced users
        if (currentUser.getUserName() == null || forcedUsername != null) {
            return true;
        }

        // Update last login date
        try {

            // Andrius This is an alternative if we need to populate full name from DB. But full name comes from Keycloak, so there is no need for that.
//            User user = em.createNamedQuery("User.getByUsername", User.class).setParameter("username", currentUser.getUserName().toLowerCase()).getSingleResult();
//            currentUser.setFullName(user.getNameOrUsername());
//
//            if (!userAuthTimeProducer.isUnsatisfied() && userAuthTimeProducer.get().getAuthTime() != currentUser.getAuthTime()) {
//                userAuthTimeProducer.get().setAuthTime(currentUser.getAuthTime());
//
//                 em.createNamedQuery("User.updateLastLoginById").setParameter("lastLoginDate", new Date()).setParameter("id", user.getId()).executeUpdate();
//
//                return nrUpdated > 0;
//            }
//            return true;
//
//        } catch (NoResultException e) {
//            return false;

            if (!userAuthTimeProducer.isUnsatisfied() && currentUser.getAuthenticationTokenId() != null && !currentUser.getAuthenticationTokenId().equals(userAuthTimeProducer.get().getAuthenticationTokenId())) {

                log.debug("User username {} updated with a new login date", currentUser.getUserName());

                int nrUpdated = em.createNamedQuery("User.updateLastLoginByUsername").setParameter("lastLoginDate", new Date()).setParameter("username", currentUser.getUserName().toLowerCase()).executeUpdate();

                if (nrUpdated > 0) {
                    userAuthTimeProducer.get().setAuthenticatedAt(currentUser.getAuthenticatedAt());
                    userAuthTimeProducer.get().setAuthenticationTokenId(currentUser.getAuthenticationTokenId());
                    return true;
                } else {
                    return false;
                }
            }

        } catch (ContextNotActiveException e) {
            // Commented out as no context is available for scheduled jobs to retrieve userAuthTimeProducer instance
            // log.error("No session context={}", e.getMessage());

        } catch (Exception e) {
            log.error("Failed to supplement current user information from db", e);
        }
        return true;
    }

    /**
     * Register a new user in application when loging in for the first time with a new user created in Keycloak
     * 
     * @param currentUser Current user information
     * @param em Entity manager
     * @param forcedUsername
     */
    public void createUserInApp(MeveoUser currentUser, EntityManager em, String forcedUsername) {

        // Takes care of anonymous or forced users
        if (currentUser.getUserName() == null || forcedUsername != null) {
            return;
        }

        // Create or retrieve current user
        try {
            User user = new User();
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
            user.setEmail(currentUser.getEmail());
            user.updateAudit(currentUser);
            em.persist(user);
            em.flush();
            log.info("A new application user was registered with username {} and name {}", user.getUserName(), user.getName() != null ? user.getName().getFullName() : "");

            if (!userAuthTimeProducer.isUnsatisfied()) {
                userAuthTimeProducer.get().setAuthenticatedAt(currentUser.getAuthenticatedAt());
                userAuthTimeProducer.get().setAuthenticationTokenId(currentUser.getAuthenticationTokenId());
            }

            triggerNewUserNotification(user, forcedUsername);

        } catch (ContextNotActiveException e) {
            // Commented out as no context is available for scheduled jobs to retrieve userAuthTimeProducer instance
            // log.error("No session context={}", e.getMessage());

        } catch (Exception e) {
            log.error("Failed to create new user in db", e);
        }
    }

    /**
     * @param user
     */
    private void triggerNewUserNotification(User user, String forcedUsername) {
        if (user != null && user.getUserName() != null && !user.getUserName().equalsIgnoreCase(forcedUsername)) {
            userEventProducer.fire(user);
        }
    }

    /**
     * Return and load if necessary a mapping between roles and permissions
     *
     * @return A mapping between roles and permissions
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, Set<String>> getRoleToPermissionMapping(String providerCode, EntityManager em) {

        synchronized (this) {
            if (roleToPermissionMapping.get(providerCode) == null) {
                try {
                    List<Role> userRoles = em.createNamedQuery("Role.getAllRoles", Role.class).getResultList();
                    Map<String, Set<String>> roleToPermissionMappingForProvider = new HashMap<>();

                    for (Role role : userRoles) {
                        Set<String> rolePermissions = new HashSet<>();
                        for (Permission permission : role.getAllPermissions()) {
                            rolePermissions.add(permission.getPermission());
                        }

                        roleToPermissionMappingForProvider.put(role.getName(), rolePermissions);
                    }
                    roleToPermissionMapping.put(providerCode, roleToPermissionMappingForProvider);
                } catch (Exception e) {
                    log.error("Failed to construct role to permission mapping", e);
                }
            }
            return roleToPermissionMapping.get(providerCode);
        }
    }

    /**
     * Get additional roles that user has assigned in application
     *
     * @param username Username to check
     * @return A set of role names that given username has in application
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Set<String> getAdditionalRoles(String username, EntityManager em) {

        // Takes care of anonymous users
        if (username == null) {
            return null;
        }

        try {
            User user = em.createNamedQuery("User.getByUsername", User.class).setParameter("username", username.toLowerCase()).getSingleResult();

            Set<String> additionalRoles = new HashSet<>();

            for (Role role : user.getRoles()) {
                additionalRoles.add(role.getName());
                for (Role subRole : role.getRoles()) {
                    additionalRoles.add(subRole.getName());
                }
            }

            return additionalRoles;

        } catch (NoResultException e) {
            return null;

        } catch (Exception e) {
            log.error("Failed to retrieve additional roles for a user {}", username, e);
            return null;
        }
    }

	/**
	 * 
	 */
	public static void invalidateRoleToPermissionMapping() {
		roleToPermissionMapping = new HashMap<>();
	}
}

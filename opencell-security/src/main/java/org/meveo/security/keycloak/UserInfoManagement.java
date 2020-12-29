package org.meveo.security.keycloak;

import org.meveo.model.admin.User;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.security.MeveoUser;
import org.meveo.security.UserAuthTimeProducer;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;

@Stateless
public class UserInfoManagement {
    @Inject
    private Logger log;


    /**
     * Map<providerCode, Map<roleName, rolePermissions>>
     */
    static Map<String, Map<String, Set<String>>> roleToPermissionMapping;

    @Inject
    private Event<User> userEventProducer;

    @Inject
    private Instance<UserAuthTimeProducer> userAuthTimeProducer;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void supplementOrCreateUserInApp(MeveoUser currentUser, EntityManager em, String forcedUsername) {

        // Takes care of anonymous users
        if (currentUser.getUserName() == null) {
            return;
        }

        // Create or retrieve current user
        try {
            User user = null;
            try {
                user = em.createNamedQuery("User.getByUsername", User.class).setParameter("username", currentUser.getUserName().toLowerCase()).getSingleResult();
                currentUser.setFullName(user.getNameOrUsername());

                if (!userAuthTimeProducer.isUnsatisfied() && userAuthTimeProducer.get().getAuthTime() != currentUser.getAuthTime()) {
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
                user.setEmail(currentUser.getEmail());
                user.updateAudit(currentUser);
                em.persist(user);
                em.flush();
                log.info("A new application user was registered with username {} and name {}", user.getUserName(), user.getName() != null ? user.getName().getFullName() : "");
                triggerNewUserNotification(user, forcedUsername);
            } catch (ContextNotActiveException e) {
                // Commented out as no context is available for scheduled jobs to retrieve userAuthTimeProducer instance
                // log.error("No session context={}", e.getMessage());
            }

        } catch (Exception e) {
            log.error("Failed to supplement current user information from db and/or create new user in db", e);
        }
    }

    /**
     * @param user
     */
    private void triggerNewUserNotification(User user, String forcedUsername) {
        if(user!=null && user.getUserName()!=null && !user.getUserName().equalsIgnoreCase(forcedUsername)) {
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
            if (roleToPermissionMapping == null || roleToPermissionMapping.get(providerCode) == null) {
                roleToPermissionMapping = new HashMap<>();

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
}

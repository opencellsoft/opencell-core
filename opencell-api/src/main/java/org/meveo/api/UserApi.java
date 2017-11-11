package org.meveo.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.SecuredEntityDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.keycloak.client.KeycloakAdminClientService;
import org.meveo.keycloak.client.KeycloakUserAccount;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.meveo.service.security.SecuredBusinessEntityService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class UserApi extends BaseApi {

    private static final String USER_HAS_NO_PERMISSION_TO_MANAGE_USERS_FOR_PROVIDER = "User has no permission to manage users for provider.";
    private static final String USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS = "User has no permission to manage other users.";
    private static final String SUPER_ADMIN_MANAGEMENT = "superAdminManagement";
    private static final String USER_SELF_MANAGEMENT = "userSelfManagement";
    private static final String USER_MANAGEMENT = "userManagement";
    private static final String ADMINISTRATION_MANAGEMENT = "administrationManagement";
    private static final String ADMINISTRATION_VISUALIZATION = "administrationVisualization";

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;
    
    @Inject
    private KeycloakAdminClientService keycloakAdminClientService;

    public void create(UserDto postData) throws MeveoApiException, BusinessException {

        boolean isSameUser = currentUser.getUserName().equals(postData.getUsername());

        if (isSameUser) {
            update(postData);
        } else {

            if (StringUtils.isBlank(postData.getUsername())) {
                missingParameters.add("username");
            }
            if (StringUtils.isBlank(postData.getEmail())) {
                missingParameters.add("email");
            }

            if ((postData.getRoles() == null || postData.getRoles().isEmpty()) && StringUtils.isBlank(postData.getRole())) {
                missingParameters.add("roles");
            }

            handleMissingParameters();

            // check if the user already exists
            if (userService.findByUsername(postData.getUsername()) != null) {
                throw new EntityAlreadyExistsException(User.class, postData.getUsername(), "username");
            }

            boolean isManagingSelf = currentUser.hasRole(USER_SELF_MANAGEMENT);
            boolean isUsersManager = currentUser.hasRole(USER_MANAGEMENT);
            boolean isSuperAdmin = currentUser.hasRole(SUPER_ADMIN_MANAGEMENT);
            boolean isAdmin = currentUser.hasRole(ADMINISTRATION_MANAGEMENT);

            boolean isManagingAllUsers = isUsersManager || isAdmin || isSuperAdmin;
            boolean isAllowed = isManagingSelf || isManagingAllUsers;
            boolean isSelfManaged = isManagingSelf && !isManagingAllUsers;

            if (!isAllowed) {
                throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS_FOR_PROVIDER);
            }

            if (isSelfManaged && !isSameUser) {
                throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS);
            }

            if (!StringUtils.isBlank(postData.getRole())) {
                if (postData.getRoles() == null) {
                    postData.setRoles(new ArrayList<String>());
                }
                postData.getRoles().add(postData.getRole());
            }
            Set<Role> roles = extractRoles(postData.getRoles());
            List<SecuredEntity> securedEntities = extractSecuredEntities(postData.getSecuredEntities());

            UserHierarchyLevel userHierarchyLevel = null;
            if (!StringUtils.isBlank(postData.getUserLevel())) {
                userHierarchyLevel = userHierarchyLevelService.findByCode(postData.getUserLevel());
                if (userHierarchyLevel == null) {
                    throw new EntityDoesNotExistsException(UserHierarchyLevel.class, "userLevel");
                }
            }

            User user = new User();
            user.setUserName(postData.getUsername().toUpperCase());
            user.setEmail((postData.getEmail()));
            Name name = new Name();
            name.setLastName(postData.getLastName());
            name.setFirstName(postData.getFirstName());
            user.setName(name);
            user.setRoles(roles);
            user.setSecuredEntities(securedEntities);
            user.setUserLevel(userHierarchyLevel);

            userService.create(user);
        }

    }

    public void update(UserDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getUsername())) {
            missingParameters.add("username");
        }
        handleMissingParameters();

        //we support old dto that containt only one role
        if (!StringUtils.isBlank(postData.getRole())) {
            if (postData.getRoles() == null) {
                postData.setRoles(new ArrayList<String>());
            }
            postData.getRoles().add(postData.getRole());
        }

        // find user
        User user = userService.findByUsername(postData.getUsername());

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, postData.getUsername(), "username");
        }

        boolean isManagingSelf = currentUser.hasRole(USER_SELF_MANAGEMENT);
        boolean isUsersManager = currentUser.hasRole(USER_MANAGEMENT);
        boolean isSuperAdmin = currentUser.hasRole(SUPER_ADMIN_MANAGEMENT);
        boolean isAdmin = currentUser.hasRole(ADMINISTRATION_MANAGEMENT);

        boolean isManagingAllUsers = isUsersManager || isAdmin || isSuperAdmin;
        boolean isAllowed = isManagingSelf || isManagingAllUsers;
        boolean isSameUser = currentUser.getUserName().equals(postData.getUsername());
        boolean isSelfManaged = isManagingSelf && !isManagingAllUsers;

        if (!isAllowed) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS_FOR_PROVIDER);
        }

        if (isSelfManaged && !isSameUser) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS);
        }

        Set<Role> roles = new HashSet<>();
        List<SecuredEntity> securedEntities = new ArrayList<>();

        if (isManagingAllUsers) {
            if (!StringUtils.isBlank(postData.getRole())) {
                if (postData.getRoles() == null) {
                    postData.setRoles(new ArrayList<String>());
                }
                postData.getRoles().add(postData.getRole());
            }
            roles.addAll(extractRoles(postData.getRoles()));
            securedEntities.addAll(extractSecuredEntities(postData.getSecuredEntities()));
        }

        UserHierarchyLevel userHierarchyLevel = null;
        if (!StringUtils.isBlank(postData.getUserLevel())) {
            userHierarchyLevel = userHierarchyLevelService.findByCode(postData.getUserLevel());
            if (userHierarchyLevel == null) {
                throw new EntityDoesNotExistsException(UserHierarchyLevel.class, "userLevel");
            }
        }

        user.setUserName(postData.getUsername());
        if (!StringUtils.isBlank(postData.getEmail())) {
            user.setEmail(postData.getEmail());
        }
        Name name = new Name();
        if (!StringUtils.isBlank(postData.getLastName())) {
            name.setLastName(postData.getLastName());
            user.setName(name);
        }
        if (!StringUtils.isBlank(postData.getFirstName())) {
            name.setFirstName(postData.getFirstName());
            user.setName(name);
        }
        if (isManagingAllUsers) {
            user.setRoles(roles);
            user.setSecuredEntities(securedEntities);
        }
        user.setUserLevel(userHierarchyLevel);

        userService.update(user);
    }

    private Set<Role> extractRoles(List<String> postDataRoles) throws EntityDoesNotExistsException {
        Set<Role> roles = new HashSet<Role>();
        if (postDataRoles == null) {
            return roles;
        }
        for (String rl : postDataRoles) {
            Role role = roleService.findByName(rl);
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, rl);
            }
            roles.add(role);
        }
        return roles;
    }

    private List<SecuredEntity> extractSecuredEntities(List<SecuredEntityDto> postDataSecuredEntities) throws EntityDoesNotExistsException {
        List<SecuredEntity> securedEntities = new ArrayList<>();
        if (postDataSecuredEntities != null) {
            SecuredEntity securedEntity = null;
            for (SecuredEntityDto securedEntityDto : postDataSecuredEntities) {
                securedEntity = new SecuredEntity();
                securedEntity.setCode(securedEntityDto.getCode());
                securedEntity.setEntityClass(securedEntityDto.getEntityClass());
                BusinessEntity businessEntity = securedBusinessEntityService.getEntityByCode(securedEntity.getEntityClass(), securedEntity.getCode());
                if (businessEntity == null) {
                    throw new EntityDoesNotExistsException(securedEntity.getEntityClass(), securedEntity.getCode());
                }
                securedEntities.add(securedEntity);
            }
        }
        return securedEntities;
    }

    public void remove(String username) throws MeveoApiException, BusinessException {
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, username, "username");
        }

        if (!(currentUser.hasRole(USER_MANAGEMENT) || currentUser.hasRole(SUPER_ADMIN_MANAGEMENT) || (currentUser.hasRole(ADMINISTRATION_MANAGEMENT)))) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS_FOR_PROVIDER);
        }

        userService.remove(user);
    }

    public UserDto find(String username) throws MeveoApiException {

        if (StringUtils.isBlank(username)) {
            missingParameters.add("username");
        }

        handleMissingParameters();

        User user = userService.findByUsernameWithFetch(username, Arrays.asList("roles", "userLevel"));

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, username, "username");
        }

        boolean isManagingSelf = currentUser.hasRole(USER_SELF_MANAGEMENT);
        boolean isUsersManager = currentUser.hasRole(USER_MANAGEMENT);
        boolean isSuperAdmin = currentUser.hasRole(SUPER_ADMIN_MANAGEMENT);
        boolean isAdmin = currentUser.hasRole(ADMINISTRATION_MANAGEMENT);
        boolean isAdminViewer = currentUser.hasRole(ADMINISTRATION_VISUALIZATION);

        boolean isManagingAllUsers = isUsersManager || isAdmin || isSuperAdmin;
        boolean isAllowed = isAdminViewer || isManagingSelf || isManagingAllUsers;
        boolean isSameUser = currentUser.getUserName().equals(username);
        boolean isSelfManaged = isManagingSelf && !isManagingAllUsers;

        if (!isAllowed) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS_FOR_PROVIDER);
        }

        if (isSelfManaged && !isSameUser) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS);
        }

        UserDto result = new UserDto(user);

        return result;
    }

    public void createOrUpdate(UserDto postData) throws MeveoApiException, BusinessException {
        User user = userService.findByUsername(postData.getUsername());
        if (user == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

	public void createKeycloakUser(UserDto postData) throws BusinessException {
		KeycloakUserAccount keycloakUserAccount = new KeycloakUserAccount();
		keycloakUserAccount.setEmail(postData.getEmail());
		keycloakUserAccount.setFirstName(postData.getFirstName());
		keycloakUserAccount.setLastName(postData.getLastName());
		keycloakUserAccount.setPassword(postData.getPassword());
		keycloakUserAccount.setUsername(postData.getUsername());

		keycloakAdminClientService.createUser(keycloakUserAccount);
	}
}

package org.meveo.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.SecuredEntityDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.UsersDto;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.filter.ObjectFilter;
import org.meveo.api.security.parameter.ObjectPropertyParser;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.keycloak.client.KeycloakAdminClientService;
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
import org.primefaces.model.SortOrder;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class UserApi extends BaseApi {

    private static final String USER_HAS_NO_PERMISSION_TO_MANAGE_USERS = "User has no permission to manage users.";
    private static final String USER_HAS_NO_PERMISSION_TO_VIEW_USERS = "User has no permission to view users.";
    private static final String USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS = "User has no permission to manage other users.";
    private static final String USER_SELF_MANAGEMENT = "userSelfManagement";
    private static final String USER_MANAGEMENT = "userManagement";
    private static final String USER_VISUALIZATION = "userVisualization";

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

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "userLevel", entityClass = UserHierarchyLevel.class, parser = ObjectPropertyParser.class))
    public void create(UserDto postData) throws MeveoApiException, BusinessException {
        create(postData, true);
    }

    public void create(UserDto postData, boolean isRequiredRoles) throws MeveoApiException, BusinessException {

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

            if (isRequiredRoles && ((postData.getRoles() == null || postData.getRoles().isEmpty()) && StringUtils.isBlank(postData.getRole()))) {
                missingParameters.add("roles");
            }

            handleMissingParameters();

            // check if the user already exists
            if (userService.findByUsername(postData.getUsername()) != null) {
                throw new EntityAlreadyExistsException(User.class, postData.getUsername(), "username");
            }

            boolean isManagingSelf = currentUser.hasRole(USER_SELF_MANAGEMENT);
            boolean isUsersManager = currentUser.hasRole(USER_MANAGEMENT);

            boolean isAllowed = isManagingSelf || isUsersManager;
            boolean isSelfManaged = isManagingSelf && !isUsersManager;

            if (!isAllowed) {
                throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS);
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
                    throw new EntityDoesNotExistsException(UserHierarchyLevel.class, postData.getUserLevel());
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

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "userLevel", entityClass = UserHierarchyLevel.class, parser = ObjectPropertyParser.class))
    public void update(UserDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getUsername())) {
            missingParameters.add("username");
        }
        handleMissingParameters();

        // we support old dto that containt only one role
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

        boolean isSameUser = currentUser.getUserName().equals(postData.getUsername());
        boolean isManagingSelf = currentUser.hasRole(USER_SELF_MANAGEMENT);
        boolean isUsersManager = currentUser.hasRole(USER_MANAGEMENT);
        boolean isAllowed = isManagingSelf || isUsersManager;
        boolean isSelfManaged = isManagingSelf && !isUsersManager;

        if (!isAllowed) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS);
        }

        if (isSelfManaged && !isSameUser) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS);
        }

        Set<Role> roles = new HashSet<>();
        List<SecuredEntity> securedEntities = new ArrayList<>();

        if (isUsersManager) {
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
                throw new EntityDoesNotExistsException(UserHierarchyLevel.class, postData.getUserLevel());
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
        if (isUsersManager) {
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

        if (!(currentUser.hasRole(USER_MANAGEMENT))) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS);
        }

        userService.remove(user);
    }

    // TODO[Andrius] Why is it here?
//    @SecuredBusinessEntityMethod(resultFilter = ObjectFilter.class)
//    @FilterResults(itemPropertiesToFilter = { @FilterProperty(property = "userLevel", entityClass = UserHierarchyLevel.class) })
    public UserDto find(HttpServletRequest httpServletRequest, String username) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(username)) {
            missingParameters.add("username");
        }

        handleMissingParameters();

        boolean isSameUser = currentUser.getUserName().equals(username);
        boolean isManagingSelf = currentUser.hasRole(USER_SELF_MANAGEMENT);
        boolean isUsersManager = currentUser.hasRole(USER_MANAGEMENT) || currentUser.hasRole(USER_VISUALIZATION);
        boolean isAllowed = isManagingSelf || isUsersManager;
        boolean isSelfManaged = isManagingSelf && !isUsersManager;

        if (!isAllowed) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS);
        }

        if (isSelfManaged && !isSameUser) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS);
        }

        User user = userService.findByUsernameWithFetch(username, Arrays.asList("roles", "userLevel"));
        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, username, "username");
        } 

        UserDto result = new UserDto(user, true);
        
        // get the external roles
        result.setExternalRoles(keycloakAdminClientService.findUserRoles(httpServletRequest, username));

        return result;
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(property = "userLevel", entityClass = UserHierarchyLevel.class, parser = ObjectPropertyParser.class))
    public void createOrUpdate(UserDto postData) throws MeveoApiException, BusinessException {
        User user = userService.findByUsername(postData.getUsername());
        if (user == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * List users matching filtering and query criteria
     * @param httpServletRequest http servlet request.
     * @param pagingAndFiltering Paging and filtering criteria. Specify "securedEntities" in fields to include the secured entities.
     * @return A list of users
     * @throws ActionForbiddenException action forbidden exception
     * @throws InvalidParameterException invalid parameter exception
     * @throws BusinessException  business exception.
     */
   // @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    //@FilterResults(propertyToFilter = "users", itemPropertiesToFilter = { @FilterProperty(property = "userLevel", entityClass = UserHierarchyLevel.class) })
    public UsersDto list(HttpServletRequest httpServletRequest, PagingAndFiltering pagingAndFiltering) throws ActionForbiddenException, InvalidParameterException, BusinessException {

        boolean isViewerSelf = currentUser.hasRole(USER_SELF_MANAGEMENT);
        boolean isAccessOthers = currentUser.hasRole(USER_MANAGEMENT) || currentUser.hasRole(USER_VISUALIZATION);

        if (!isViewerSelf && !isAccessOthers) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_VIEW_USERS);
        }

        if (isViewerSelf && !isAccessOthers) {
            if (pagingAndFiltering == null) {
                pagingAndFiltering = new PagingAndFiltering("userName:" + currentUser.getUserName(), null, null, null, null, null);
            } else {
                pagingAndFiltering.getFilters().put("userName", currentUser.getUserName());
            }
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("userName", SortOrder.ASCENDING, null, pagingAndFiltering, User.class);

        Long totalCount = userService.count(paginationConfig);

        UsersDto result = new UsersDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<User> users = userService.list(paginationConfig);
            for (User user : users) {
                UserDto userDto = new UserDto(user, pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("securedEntities"));
                userDto.setExternalRoles(keycloakAdminClientService.findUserRoles(httpServletRequest, user.getUserName()));
                result.getUsers().add(userDto);
            }
        }

        return result;
    }

    public String createExternalUser(HttpServletRequest httpServletRequest, UserDto postData) throws BusinessException, MeveoApiException {
        // create the user in core
        create(postData, false);

        return keycloakAdminClientService.createUser(httpServletRequest, postData);
    }

    public void updateExternalUser(HttpServletRequest httpServletRequest, UserDto postData) throws BusinessException, MeveoApiException {
        // update user in core
        update(postData);

        keycloakAdminClientService.updateUser(httpServletRequest, postData);
    }

    public void deleteExternalUser(HttpServletRequest httpServletRequest, String username) throws BusinessException, MeveoApiException {
        // delete in core
        remove(username);

        keycloakAdminClientService.deleteUser(httpServletRequest, username);
    }
    
}

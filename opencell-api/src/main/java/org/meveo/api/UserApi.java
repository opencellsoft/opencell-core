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

package org.meveo.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.CurrentUserDto;
import org.meveo.api.dto.SecuredEntityDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.UsersDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.security.SecuredBusinessEntityService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
//@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class UserApi extends BaseApi {

    private static final String USER_HAS_NO_PERMISSION_TO_MANAGE_USERS = "User has no permission to manage users.";
    private static final String USER_HAS_NO_PERMISSION_TO_VIEW_USERS = "User has no permission to view users.";
    private static final String USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS = "User has no permission to manage other users.";
    private static final String ROLE_API_USER_SELF_MANAGEMENT = "apiUserSelfManagement";
    private static final String ROLE_API_USER_MANAGEMENT = "apiUserManagement";
    private static final String ROLE_API_USER_VISUALIZATION = "apiUserVisualization";

    @Inject
    private UserService userService;
    
    @Inject
    private RoleService roleService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    public void create(UserDto postData) throws MeveoApiException, BusinessException {
        create(postData, false);
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
            if (isRequiredRoles && (postData.getRoles() == null || postData.getRoles().isEmpty())) {
                missingParameters.add("userRole");
            }

            handleMissingParameters();

            // check if the user already exists
            if (userService.findByUsername(postData.getUsername(), false, false) != null) {
                throw new EntityAlreadyExistsException(User.class, postData.getUsername(), "username");
            }

            boolean isManagingSelf = currentUser.hasRole(ROLE_API_USER_SELF_MANAGEMENT);
            boolean isUsersManager = currentUser.hasRole(ROLE_API_USER_MANAGEMENT);

            boolean isAllowed = isManagingSelf || isUsersManager;
            boolean isSelfManaged = isManagingSelf && !isUsersManager;

            if (!isAllowed) {
                throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS);
            }

            if (isSelfManaged && !isSameUser) {
                throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS);
            }

            // Validate secured entities
            List<SecuredEntity> securedEntities = extractSecuredEntities(postData.getSecuredEntities());

            User user = new User();
            user.setUserName(postData.getUsername().toUpperCase());
            user.setPassword(postData.getPassword());
            user.setEmail((postData.getEmail()));
            user.setName(new Name(null, postData.getFirstName(), postData.getLastName()));
            user.setRoles(new HashSet<>(postData.getRoles()));
            user.setUserLevel(postData.getUserLevel());
            if (postData.getCustomFields() != null) {
                super.populateCustomFields(postData.getCustomFields(), user, true, true);
            }
            userService.create(user);
            addUserRoles(user.getId(), postData.getRoles());

            // Save secured entities
            securedBusinessEntityService.syncSecuredEntitiesForUser(securedEntities, postData.getUsername());
        }

    }

    public void update(UserDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getUsername())) {
            missingParameters.add("username");
        }
        handleMissingParameters();

        // find user
        User user = userService.findByUsername(postData.getUsername(), false, true);

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, postData.getUsername(), "username");
        }

        boolean isSameUser = currentUser.getUserName().equals(postData.getUsername());
        boolean isManagingSelf = currentUser.hasRole(ROLE_API_USER_SELF_MANAGEMENT);
        boolean isUsersManager = currentUser.hasRole(ROLE_API_USER_MANAGEMENT);
        boolean isAllowed = isManagingSelf || isUsersManager;
        boolean isSelfManaged = isManagingSelf && !isUsersManager;

        if (!isAllowed) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS);
        }

        if (isSelfManaged && !isSameUser) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS);
        }

        // Validate secured entities
        List<SecuredEntity> securedEntities = null;

        if (postData.getSecuredEntities() != null) {
            if (postData.getSecuredEntities().isEmpty()) {
                securedEntities = new ArrayList<>();

            } else {
                securedEntities = extractSecuredEntities(postData.getSecuredEntities());
            }
        }

        user.setPassword(postData.getPassword());
        if (postData.getEmail() != null) {
            user.setEmail(postData.getEmail());
        }

        if (postData.getFirstName() != null || postData.getLastName() != null) {
            if (user.getName() == null) {
                user.setName(new Name());
            }
            if (postData.getFirstName() != null) {
                user.getName().setFirstName(postData.getFirstName());
            }
            if (postData.getLastName() != null) {
                user.getName().setLastName(postData.getLastName());
            }
        }
        if (postData.getRoles() != null) {
            user.setRoles(new HashSet<>(postData.getRoles()));
        }

        if (postData.getUserLevel() != null) {
            user.setUserLevel(postData.getUserLevel());
        }
        if (postData.getCustomFields() != null) {
            populateCustomFields(postData.getCustomFields(), user, false, true);
        }

        userService.updateUserWithAttributes(user, postData.getAttributes());
        
        addUserRoles(user.getId(), postData.getRoles());

        // Save secured entities
        if (securedEntities != null) {
            securedBusinessEntityService.syncSecuredEntitiesForUser(securedEntities, postData.getUsername());
        }
    }

    private List<SecuredEntity> extractSecuredEntities(List<SecuredEntityDto> securedEntityDtos) throws EntityDoesNotExistsException {
        List<SecuredEntity> securedEntities = new ArrayList<>();
        if (securedEntityDtos != null) {
            SecuredEntity securedEntity = null;
            for (SecuredEntityDto securedEntityDto : securedEntityDtos) {
                securedEntity = new SecuredEntity();
                securedEntity.setEntityId(securedEntityDto.getEntityId());
                securedEntity.setEntityCode(securedEntityDto.getEntityCode());
                securedEntity.setEntityClass(securedEntityDto.getEntityClass());
                securedEntity.setPermission(securedEntityDto.getPermission());
                securedEntity.setDisabled(securedEntityDto.isDisabled());
                BusinessEntity businessEntity = securedBusinessEntityService.getEntityByCode(securedEntity.getEntityClass(), securedEntity.getEntityCode());
                if (businessEntity == null) {
                    throw new EntityDoesNotExistsException(securedEntity.getEntityClass(), securedEntity.getEntityCode());
                }
                securedEntity.setEntityId(businessEntity.getId());
                securedEntities.add(securedEntity);
            }
        }
        return securedEntities;
    }

    public void remove(String username) throws MeveoApiException, BusinessException {
        User user = userService.findByUsername(username, false, false);

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, username, "username");
        }

        userService.remove(user);
    }

    // TODO[Andrius] Why is it here?
//    @SecuredBusinessEntityMethod(resultFilter = ObjectFilter.class)
//    @FilterResults(itemPropertiesToFilter = { @FilterProperty(property = "userLevel", entityClass = UserHierarchyLevel.class) })
    public UserDto find(String username) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(username)) {
            missingParameters.add("username");
        }

        handleMissingParameters();
        checkPermissions(username);

        User user = userService.findByUsername(username, true, false);

        if (user == null) {
            throw new EntityDoesNotExistsException(User.class, username, "username");
        }

        UserDto userDto = new UserDto(user);
        getKeycloakAttributesByUsername(userDto);

        List<SecuredEntity> securedEntities = securedBusinessEntityService.getSecuredEntitiesForUser(username);
        if (securedEntities != null) {
            userDto.setSecuredEntities(securedEntities.stream().map(SecuredEntityDto::new).collect(Collectors.toList()));
        }
        userDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(user));
        return userDto;
    }

    private void checkPermissions(String username) {
        boolean isSameUser = currentUser.getUserName().equals(username);
        boolean isManagingSelf = currentUser.hasRole(ROLE_API_USER_SELF_MANAGEMENT);
        boolean isUsersManager = currentUser.hasRole(ROLE_API_USER_MANAGEMENT) || currentUser.hasRole(ROLE_API_USER_VISUALIZATION);
        boolean isAllowed = isManagingSelf || isUsersManager;
        boolean isSelfManaged = isManagingSelf && !isUsersManager;

        if (!isAllowed) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_USERS);
        }

        if (isSelfManaged && !isSameUser) {
            throw new ActionForbiddenException(USER_HAS_NO_PERMISSION_TO_MANAGE_OTHER_USERS);
        }
    }

    /**
     * Get keycloak attributes by given username
     * @param userDto {@link UserDto}
     */
    private void getKeycloakAttributesByUsername(UserDto userDto) {
        UserRepresentation userRepresentation = userService.getUserRepresentationByUsername(userDto.getUsername());

        if(userRepresentation != null) {
            Map<String, List<String>> keycloakAttributes = userRepresentation.getAttributes();

            if(keycloakAttributes != null && !keycloakAttributes.isEmpty()) {
                Map<String, String> attributes = new HashMap<>();

                for (Map.Entry<String, List<String>> entry : keycloakAttributes.entrySet()) {
                    attributes.put(entry.getKey(), String.join(", ", entry.getValue()));
                }

                userDto.setAttributes(attributes);
            }
        }
    }

    public void createOrUpdate(UserDto postData) throws MeveoApiException, BusinessException {
        User user = userService.findByUsername(postData.getUsername(), false, false);
        if (user == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * List users matching filtering and query criteria
     * 
     * @param pagingAndFiltering Paging and filtering criteria. Specify "securedEntities" in fields to include the secured entities.
     * @return A list of users
     * @throws ActionForbiddenException action forbidden exception
     * @throws InvalidParameterException invalid parameter exception
     * @throws BusinessException business exception.
     */
    // @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    // @FilterResults(propertyToFilter = "users", itemPropertiesToFilter = { @FilterProperty(property = "userLevel", entityClass = UserHierarchyLevel.class) })
    public UsersDto list(PagingAndFiltering pagingAndFiltering) throws ActionForbiddenException, InvalidParameterException, BusinessException {

        boolean isViewerSelf = currentUser.hasRole(ROLE_API_USER_SELF_MANAGEMENT);
        boolean isAccessOthers = currentUser.hasRole(ROLE_API_USER_MANAGEMENT) || currentUser.hasRole(ROLE_API_USER_VISUALIZATION);

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
                UserDto userDto = new UserDto(user);
                getKeycloakAttributesByUsername(userDto);
                if (pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("securedEntities")) {
                    List<SecuredEntity> securedEntities = securedBusinessEntityService.getSecuredEntitiesForUser(user.getUserName());
                    if (securedEntities != null) {
                        userDto.setSecuredEntities(securedEntities.stream().map(SecuredEntityDto::new).collect(Collectors.toList()));
                    }
                }
                userDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(user));
                result.getUsers().add(userDto);
            }
        }

        return result;
    }

    public CurrentUserDto getCurrentUser() throws MeveoApiException, BusinessException {

        Map<String, List<String>> rolesByApplication = currentUserProvider.getRolesByApplication(currentUser);
        CurrentUserDto dto = new CurrentUserDto(currentUser, rolesByApplication);

        return dto;
    }
    
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cleanRoles(Long idUser){
        User user = userService.findById(idUser);
        if(user == null){
            throw new EntityDoesNotExistsException(User.class, idUser);
        }
        user.getUserRoles().clear();
        userService.update(user);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeRole(Long userId, Long roleId){
        User user = userService.findById(userId);
        if(user == null)
            throw new EntityDoesNotExistsException(User.class, userId);
        Optional<Role> role = user.getUserRoles().stream()
                .filter(r -> r.getId().equals(roleId))
                .findFirst();
        if(role.isPresent()){
            user.getUserRoles().remove(role.get());
            userService.update(user);
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addRole(Long userId, Long roleId){
        User user = userService.findById(userId);
        if(user == null)
            throw new EntityDoesNotExistsException(User.class, userId);
        Role role = roleService.findById(roleId);
        if(role == null)
            throw new EntityDoesNotExistsException(Role.class, roleId);
        user.getUserRoles().add(role);
        userService.update(user);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addRoles(Long userId, List<Long> rolesId){
        User user = userService.findById(userId);
        if(user == null)
            throw new EntityDoesNotExistsException(User.class, userId);
        Set<Role> roles = rolesId.stream()
                .map(roleId -> {
                    Role role = roleService.findById(roleId);
                    if (role == null)
                        throw new EntityDoesNotExistsException(Role.class, roleId);
                    return role;
                }).collect(Collectors.toSet());
        user.getUserRoles().addAll(roles);
        userService.update(user);
    }
    
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addUserRoles(Long userId, List<String> roleCodes){
        User user = userService.findById(userId);
        if(user == null)
            throw new EntityDoesNotExistsException(User.class, userId);
        Set<Role> roles = roleCodes.stream()
                .map(roleCode -> {
                    Role role = roleService.findByName(roleCode);
                    if (role == null && !userService.canSynchroWithKC()) //throw an exception only when the master is OC
                        throw new EntityDoesNotExistsException(Role.class, roleCode);
                    return role;
                }).collect(Collectors.toSet());
        roles.removeIf(Objects::isNull);
        user.getUserRoles().addAll(roles);
        userService.update(user);
    }
	
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void cleanAllAccessibleEntities(Long id){
		User user = userService.findById(id);
		if(user == null)
			throw new EntityDoesNotExistsException(User.class, id);
		var securedEntity = securedBusinessEntityService.getSecuredEntitiesForUser(user.getUserName());
		if(CollectionUtils.isNotEmpty(securedEntity))
			securedBusinessEntityService.remove(securedEntity.stream().map(SecuredEntity::getId).collect(Collectors.toSet()));
	}
	
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void removeAccessibleEntity(Long id, Long securedEntityId){
		if(securedEntityId == null){
			missingParameters.add("username");
		}
		handleMissingParameters();
		User user = userService.findById(id);
		if(user == null)
			throw new EntityDoesNotExistsException(User.class, id);
		SecuredEntity securedEntity = securedBusinessEntityService.findById(securedEntityId);
		if(securedEntity == null){
			throw new EntityDoesNotExistsException(SecuredEntity.class, securedEntityId);
		}
		securedBusinessEntityService.remove(securedEntity);
	}
	
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void addAccessibleEntity(Long id,  SecuredEntityDto securedEntity){
		User user = userService.findById(id);
		if(user == null)
			throw new EntityDoesNotExistsException(User.class, id);
		List<SecuredEntity> securedEntities =  extractSecuredEntities(List.of(securedEntity));
		securedBusinessEntityService.addSecuredEntityForUser(securedEntities.get(0), user.getUserName());
	}
	
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void addAccessibleEntities(Long id, List<SecuredEntityDto> securedEntitiesDtos){
		if(CollectionUtils.isNotEmpty(securedEntitiesDtos)){
			List<SecuredEntity> securedEntities =  extractSecuredEntities(securedEntitiesDtos);
			User user = userService.findById(id);
			if(user == null)
				throw new EntityDoesNotExistsException(User.class, id);
			securedEntities.forEach(securedEntity -> securedBusinessEntityService.addSecuredEntityForUser(securedEntity, user.getUserName()));
		}
	}

}
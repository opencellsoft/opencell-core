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
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.RolesDto;
import org.meveo.api.dto.SecuredEntityDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.security.SecuredBusinessEntityService;

import liquibase.repackaged.org.apache.commons.lang3.BooleanUtils;

/**
 * API class for managing roles
 */
@Stateless
public class RoleApi extends BaseApi {

    @Inject
    private RoleService roleService;

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    /**
     * 
     * @param postData posted data to API
     * 
     * @return Role entity
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException meveo api exception
     */
    public Role create(RoleDto postData) throws MeveoApiException, BusinessException {

        String name = postData.getName();
        if (StringUtils.isBlank(name)) {
            missingParameters.add("name");
        }

        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }

        handleMissingParameters();
        
        if(BooleanUtils.isFalse(postData.getCreateInKC())) {
        	if(roleService.findByName(name)!=null){
        		throw new EntityAlreadyExistsException(Role.class, name, "role name");
        	}
        }
        if (BooleanUtils.isTrue(postData.getCreateInKC())) {
        	if(roleService.findByName(name, false, false) != null) {
        		throw new EntityAlreadyExistsException(Role.class, name, "role name");
        	}
        }
        
        Role role = new Role();
        role.setName(name);
        role.setDescription(postData.getDescription());
        if(postData.getCreateInKC()!=null){
        role.setCreateInKC(postData.getCreateInKC());
        }
        

//        // Create/Update and add child roles
//        if (postData.getRoles() != null && !postData.getRoles().isEmpty()) {
//            for (RoleDto roleDto : postData.getRoles()) {
//                role.getRoles().add(createOrUpdate(roleDto));
//            }
//        }

        // Validate secured entities
        List<SecuredEntity> securedEntities = extractSecuredEntities(postData.getSecuredEntities());

        try {
            populateCustomFields(postData.getCustomFields(), role, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        roleService.create(role);

        // Save secured entities
        securedBusinessEntityService.syncSecuredEntitiesForRole(securedEntities, name);

        return role;
    }

    /**
     * Update role.
     * 
     * @param postData Role DTO
     * 
     * @return Updated Role entity
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public Role update(RoleDto postData) throws MeveoApiException, BusinessException {
    	 Role role=null;
        String name = postData.getName();
        if (StringUtils.isBlank(name)) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage roles");
        }
        if(BooleanUtils.isFalse(postData.getUpdateInKC())){
        	role = roleService.findByName(name);	
        }else {
        	role = roleService.findByName(name, false, false);
        }

        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }

        if (postData.getDescription() != null) {
            role.setDescription(postData.getDescription());
        }
        
        if(postData.getUpdateInKC()!=null){
        	role.setUpdateInKC(postData.getUpdateInKC());
        }

//        // Create/Update and add child roles
//        if (postData.getRoles() != null && !postData.getRoles().isEmpty()) {
//            for (RoleDto roleDto : postData.getRoles()) {
//                role.getRoles().add(createOrUpdate(roleDto));
//            }
//        }

        // Validate secured entities
        List<SecuredEntity> securedEntities = null;

        if (postData.getSecuredEntities() != null) {
            if (postData.getSecuredEntities().isEmpty()) {
                securedEntities = new ArrayList<>();

            } else {
                securedEntities = extractSecuredEntities(postData.getSecuredEntities());
            }
        }

        try {
            populateCustomFields(postData.getCustomFields(), role, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        role = roleService.update(role);

        // Save secured entities
        if (securedEntities != null) {
            securedBusinessEntityService.syncSecuredEntitiesForRole(securedEntities, name);
        }
        return role;
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

    public RoleDto find(String name, boolean includeSecuredEntities) throws MeveoApiException {

        if (StringUtils.isBlank(name)) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        Role role = roleService.findByName(name, true);
        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }
        RoleDto roleDto = new RoleDto(role);
        if (includeSecuredEntities) {
            List<SecuredEntity> securedEntities = securedBusinessEntityService.getSecuredEntitiesForRole(name);
            if (securedEntities != null) {
                roleDto.setSecuredEntities(securedEntities.stream().map(SecuredEntityDto::new).collect(Collectors.toList()));
            }
        }
        return roleDto;
    }

    public void remove(String name) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(name)) {
            missingParameters.add("role");
        }

        handleMissingParameters();

        Role role = roleService.findByName(name, false, false);
        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }
        roleService.remove(role);
    }

    public void createOrUpdate(RoleDto postData) throws MeveoApiException, BusinessException {
        Role role=null;
        String name = postData.getName();
        if (name == null) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        if(BooleanUtils.isFalse(postData.getUpdateInKC())){
        	role = roleService.findByName(name);	
        }else {
        	role = roleService.findByName(name, false, false);
        }
        
        if (role == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * List roles matching filtering and query criteria.
     * 
     * @param pagingAndFiltering Paging and filtering criteria. Specify "permissions" in fields to include the permissions. Specify "roles" to include child roles.
     * @return A list of roles
     * @throws ActionForbiddenException action forbidden exception
     * @throws InvalidParameterException invalid parameter exception.
     */
    @SuppressWarnings("rawtypes")
    public RolesDto list(PagingAndFiltering pagingAndFiltering) throws ActionForbiddenException, InvalidParameterException {

        PaginationConfiguration paginationConfig = toPaginationConfiguration("name", SortOrder.ASCENDING, null, pagingAndFiltering, (Class) null);

        roleService.list(paginationConfig);

        RolesDto result = new RolesDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());

        List<Role> roles = roleService.list(paginationConfig);
        result.getPaging().setTotalNumberOfRecords(roles.size());
        for (Role role : roles) {
            result.getRoles().add(new RoleDto(role));
        }

        return result;
    }
}
package org.meveo.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.PermissionDto;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.admin.impl.RoleService;

@Stateless
public class RoleApi extends BaseApi {

    @Inject
    private RoleService roleService;

    @Inject
    private PermissionService permissionService;

    /**
     * 
     * @param postData

     * @return Role entity
     * @throws MeveoApiException
     * @throws BusinessException
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

        if (roleService.findByName(name) != null) {
            throw new EntityAlreadyExistsException(Role.class, name, "role name");
        }

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage roles for provider");
        }

        Role role = new Role();
        role.setName(name);
        role.setDescription(postData.getDescription());
        
        List<PermissionDto> permissionDtos = postData.getPermission();
        if (permissionDtos != null && !permissionDtos.isEmpty()) {
            Set<Permission> permissions = new HashSet<Permission>();

            for (PermissionDto permissionDto : permissionDtos) {
                boolean found = false;

                List<Permission> permissionsFromDB = permissionService.list();

                Permission p = null;
                for (Permission permission : permissionsFromDB) {
                    if (permission.getName().equals(permissionDto.getName())) {
                        found = true;
                        p = permission;
                        break;
                    }
                }

                if (found) {
                    permissions.add(p);
                } else {
                    throw new EntityDoesNotExistsException(Permission.class, permissionDto.getName(), "name");
                }
            }
            role.setPermissions(permissions);
        }

        // Create/Update and add child roles
        if (postData.getRoles() != null && !postData.getRoles().isEmpty()) {
            for (RoleDto roleDto : postData.getRoles()) {
                role.getRoles().add(createOrUpdate(roleDto));
            }
        }

        roleService.create(role);

        return role;
    }

    /**
     * Update role
     * 
     * @param postData Role DTO

     * @return Updated Role entity
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public Role update(RoleDto postData) throws MeveoApiException, BusinessException {

        String name = postData.getName();
        if (StringUtils.isBlank(name)) {
            missingParameters.add("name");
        }

        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }

        handleMissingParameters();

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage roles for provider");
        }

        Role role = roleService.findByName(name);

        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }

        role.setDescription(postData.getDescription());

        List<PermissionDto> permissionDtos = postData.getPermission();
        if (permissionDtos != null && !permissionDtos.isEmpty()) {
            Set<Permission> permissions = new HashSet<Permission>();

            for (PermissionDto permissionDto : permissionDtos) {
                boolean found = false;

                List<Permission> permissionsFromDB = permissionService.list();

                Permission p = null;
                for (Permission permission : permissionsFromDB) {
                    if (permission.getName().equals(permissionDto.getName())) {
                        found = true;
                        p = permission;
                        break;
                    }
                }

                if (found) {
                    permissions.add(p);
                } else {
                    throw new EntityDoesNotExistsException(Permission.class, permissionDto.getName(), "name");
                }
            }
            role.setPermissions(permissions);
        }

        // Create/Update and add child roles
        if (postData.getRoles() != null && !postData.getRoles().isEmpty()) {
            for (RoleDto roleDto : postData.getRoles()) {
                role.getRoles().add(createOrUpdate(roleDto));
            }
        }

        return roleService.update(role);
    }

    public RoleDto find(String name) throws MeveoApiException {

        if (StringUtils.isBlank(name)) {
            missingParameters.add("roleName");
        }

        handleMissingParameters();

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationVisualization")))) {
            throw new ActionForbiddenException("User has no permission to access roles for provider");
        }

        RoleDto roleDto = null;
        Role role = roleService.findByName(name);
        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }
        roleDto = new RoleDto(role);

        return roleDto;
    }

    public void remove(String name) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(name)) {
            missingParameters.add("role");
        }

        handleMissingParameters();

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage roles for provider");
        }

        Role role = roleService.findByName(name);
        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }
        role.setPermissions(null);
        roleService.remove(role);
    }

    public Role createOrUpdate(RoleDto postData) throws MeveoApiException, BusinessException {

        String name = postData.getName();
        if (name == null) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage roles for provider");
        }

        Role role = roleService.findByName(name);
        if (role == null) {
            return create(postData);
        } else {
            return update(postData);
        }
    }
}
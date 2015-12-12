package org.meveo.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.PermissionDto;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
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
     * @param currentUser
     * @return Role entity
     * @throws MeveoApiException
     */
    public Role create(RoleDto postData, User currentUser) throws MeveoApiException {

        String name = postData.getName();
        if (name == null) {
            missingParameters.add("name");
            throw new MissingParameterException(getMissingParametersExceptionMessage());
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
                role.getRoles().add(createOrUpdate(roleDto, currentUser));
            }
        }

        roleService.create(role, currentUser, currentUser.getProvider());

        return role;
    }

    /**
     * Update role
     * 
     * @param postData Role DTO
     * @param currentUser Current user
     * @return Updated Role entity
     * @throws MeveoApiException
     */
    public Role update(RoleDto postData, User currentUser) throws MeveoApiException {

        String name = postData.getName();
        if (name == null) {
            missingParameters.add("name");
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }
        Role role = roleService.findByName(name, currentUser.getProvider());

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
                role.getRoles().add(createOrUpdate(roleDto, currentUser));
            }
        }

        return roleService.update(role, currentUser);
    }

    public RoleDto find(String name, User currentUser) throws MeveoApiException {
        RoleDto roleDto = null;
        if (name != null) {
            Role role = roleService.findByName(name, currentUser.getProvider());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, name, "name");
            }
            roleDto = new RoleDto(role);
        }
        return roleDto;
    }

    public void remove(String name, User currentUser) throws MeveoApiException {

        if (name != null) {
            Role role = roleService.findByName(name, currentUser.getProvider());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, name, "name");
            }
            role.setPermissions(null);
            roleService.remove(role);
        }
    }

    public Role createOrUpdate(RoleDto postData, User currentUser) throws MeveoApiException {

        String name = postData.getName();
        if (name == null) {
            missingParameters.add("name");
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        Role role = roleService.findByName(name, currentUser.getProvider());
        if (role == null) {
            return create(postData, currentUser);
        } else {
            return update(postData, currentUser);
        }
    }
}
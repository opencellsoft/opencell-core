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
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.LoginException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.crm.impl.ProviderService;

@Stateless
public class RoleApi extends BaseApi {

    @Inject
    private ProviderService providerService;

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
     * @throws BusinessException
     */
    public Role create(RoleDto postData, User currentUser) throws MeveoApiException, BusinessException {

        String name = postData.getName();
        if (StringUtils.isBlank(name)) {
            missingParameters.add("name");
        }

        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }

        handleMissingParameters();

        // Find provider and check if user has access to manage that provider data
        Provider provider = null;
        if (!StringUtils.isBlank(postData.getProvider())) {
            provider = providerService.findByCode(postData.getProvider());
            if (provider == null) {
                throw new EntityDoesNotExistsException(Provider.class, postData.getProvider());
            }
        } else {
            provider = currentUser.getProvider();
        }

        if (roleService.findByName(name, provider) != null) {
            throw new EntityAlreadyExistsException(Role.class, name, "role name");
        }

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationManagement") && provider
            .equals(currentUser.getProvider())))) {
            throw new LoginException("User has no permission to manage roles for provider " + provider.getCode());
        }

        Role role = new Role();
        role.setName(name);
        role.setDescription(postData.getDescription());
        role.setProvider(provider);

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

        roleService.create(role, currentUser);

        return role;
    }

    /**
     * Update role
     * 
     * @param postData Role DTO
     * @param currentUser Current user
     * @return Updated Role entity
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public Role update(RoleDto postData, User currentUser) throws MeveoApiException, BusinessException {

        String name = postData.getName();
        if (StringUtils.isBlank(name)) {
            missingParameters.add("name");
        }

        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }

        handleMissingParameters();

        // Find provider and check if user has access to manage that provider data
        Provider provider = null;
        if (!StringUtils.isBlank(postData.getProvider())) {
            provider = providerService.findByCode(postData.getProvider());
            if (provider == null) {
                throw new EntityDoesNotExistsException(Provider.class, postData.getProvider());
            }
        } else {
            provider = currentUser.getProvider();
        }

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationManagement") && provider
            .equals(currentUser.getProvider())))) {
            throw new LoginException("User has no permission to manage roles for provider " + provider.getCode());
        }

        Role role = roleService.findByName(name, provider);

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

    public RoleDto find(String name, String providerCode, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(name)) {
            missingParameters.add("roleName");
        }

        handleMissingParameters();

        // Find provider and check if user has access to manage that provider data
        Provider provider = null;
        if (!StringUtils.isBlank(providerCode)) {
            provider = providerService.findByCode(providerCode);
            if (provider == null) {
                throw new EntityDoesNotExistsException(Provider.class, providerCode);
            }
        } else {
            provider = currentUser.getProvider();
        }

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationVisualization") && provider
            .equals(currentUser.getProvider())))) {
            throw new LoginException("User has no permission to access roles for provider " + provider.getCode());
        }

        RoleDto roleDto = null;
        Role role = roleService.findByName(name, provider);
        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }
        roleDto = new RoleDto(role);

        return roleDto;
    }

    public void remove(String name, String providerCode, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(name)) {
            missingParameters.add("role");
        }

        handleMissingParameters();

        // Find provider and check if user has access to manage that provider data
        Provider provider = null;
        if (!StringUtils.isBlank(providerCode)) {
            provider = providerService.findByCode(providerCode);
            if (provider == null) {
                throw new EntityDoesNotExistsException(Provider.class, providerCode);
            }
        } else {
            provider = currentUser.getProvider();
        }

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationManagement") && provider
            .equals(currentUser.getProvider())))) {
            throw new LoginException("User has no permission to manage roles for provider " + provider.getCode());
        }

        Role role = roleService.findByName(name, provider);
        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }
        role.setPermissions(null);
        roleService.remove(role, currentUser);
    }

    public Role createOrUpdate(RoleDto postData, User currentUser) throws MeveoApiException, BusinessException {

        String name = postData.getName();
        if (name == null) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        // Find provider and check if user has access to manage that provider data
        Provider provider = null;
        if (!StringUtils.isBlank(postData.getProvider())) {
            provider = providerService.findByCode(postData.getProvider());
            if (provider == null) {
                throw new EntityDoesNotExistsException(Provider.class, postData.getProvider());
            }
        } else {
            provider = currentUser.getProvider();
        }

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationManagement") && provider
            .equals(currentUser.getProvider())))) {
            throw new LoginException("User has no permission to manage roles for provider " + provider.getCode());
        }

        Role role = roleService.findByName(name, provider);
        if (role == null) {
            return create(postData, currentUser);
        } else {
            return update(postData, currentUser);
        }
    }
}
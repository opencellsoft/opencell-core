package org.meveo.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.PermissionDto;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.RolesDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.keycloak.client.KeycloakAdminClientService;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.admin.impl.RoleService;
import org.primefaces.model.SortOrder;

@Stateless
public class RoleApi extends BaseApi {

    @Inject
    private RoleService roleService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private KeycloakAdminClientService keycloakAdminClientService;

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

        if (roleService.findByName(name) != null) {
            throw new EntityAlreadyExistsException(Role.class, name, "role name");
        }

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage roles.");
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
     * Update role.
     * 
     * @param postData Role DTO
     * 
     * @return Updated Role entity
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public Role update(RoleDto postData) throws MeveoApiException, BusinessException {

        String name = postData.getName();
        if (StringUtils.isBlank(name)) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage roles");
        }

        Role role = roleService.findByName(name);

        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }

        if (postData.getDescription() != null) {
            role.setDescription(postData.getDescription());
        }

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
            throw new ActionForbiddenException("User has no permission to access roles");
        }

        RoleDto roleDto = null;
        Role role = roleService.findByName(name);
        if (role == null) {
            throw new EntityDoesNotExistsException(Role.class, name, "name");
        }
        roleDto = new RoleDto(role, true, true);

        return roleDto;
    }

    public void remove(String name) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(name)) {
            missingParameters.add("role");
        }

        handleMissingParameters();

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage roles");
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
            throw new ActionForbiddenException("User has no permission to manage roles");
        }

        Role role = roleService.findByName(name);
        if (role == null) {
            return create(postData);
        } else {
            return update(postData);
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
    public RolesDto list(PagingAndFiltering pagingAndFiltering) throws ActionForbiddenException, InvalidParameterException {

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationVisualization")))) {
            throw new ActionForbiddenException("User has no permission to access roles");
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("name", SortOrder.ASCENDING, null, pagingAndFiltering, Role.class);

        Long totalCount = roleService.count(paginationConfig);

        RolesDto result = new RolesDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<Role> roles = roleService.list(paginationConfig);
            for (Role role : roles) {
                result.getRoles().add(new RoleDto(role, pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("roles"),
                    pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("permissions")));
            }
        }

        return result;
    }

    public List<RoleDto> listExternalRoles(HttpServletRequest httpServletRequest) throws BusinessException {
        return keycloakAdminClientService.listRoles(httpServletRequest);
    }

}
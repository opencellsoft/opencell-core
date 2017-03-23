package org.meveo.api;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.PermissionDto;
import org.meveo.api.dto.PermissionsDto;
import org.meveo.model.security.Permission;
import org.meveo.service.admin.impl.PermissionService;

@Stateless
public class PermissionApi extends BaseApi {

    @Inject
    private PermissionService permissionService;

    public PermissionsDto list() {
        PermissionsDto permissionsDto = new PermissionsDto();

        List<Permission> permissions = permissionService.list();
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission p : permissions) {
                PermissionDto pd = new PermissionDto(p);
                permissionsDto.getPermission().add(pd);
            }
        }

        return permissionsDto;
    }
}
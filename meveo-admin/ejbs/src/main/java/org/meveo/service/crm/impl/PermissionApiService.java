package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.PermissionDto;
import org.meveo.api.dto.PermissionsDto;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Permission;
import org.meveo.service.admin.impl.PermissionService;

@Stateless
public class PermissionApiService extends BaseApi {
	
	@Inject
	private PermissionService permissionService;
	
	public PermissionsDto list(Provider provider) {
		PermissionsDto permissionsDto = new PermissionsDto();
		
		List<Permission> permissions = permissionService.list(provider);
		if (permissions != null && !permissions.isEmpty()) {
			for (Permission p: permissions) {
				PermissionDto pd = new PermissionDto(p);
				permissionsDto.getPermission().add(pd);
			}
		}
		
		return permissionsDto;
	}
}

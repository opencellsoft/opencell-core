package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.PermissionsDto;
import org.meveo.model.crm.Provider;
import org.meveo.service.crm.impl.PermissionApiService;

@Stateless
public class PermissionApi extends BaseApi {
	
	@Inject
	private PermissionApiService permissionApiService;
	
	public PermissionsDto list(Provider provider) {
		return permissionApiService.list(provider);
	}
	
}

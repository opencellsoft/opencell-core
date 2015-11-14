package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

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
	 * @throws MeveoApiException
	 */
	public void create(RoleDto postData, User currentUser)
			throws MeveoApiException {

		String name = postData.getName();
		if (name != null) {
			Role role = new Role();
			role.setName(name);
			role.setDescription(postData.getDescription());

			List<PermissionDto> permissionDtos = postData.getPermission();
			if (permissionDtos != null && !permissionDtos.isEmpty()) {
				List<Permission> permissions = new ArrayList<Permission>();
				
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

			roleService.create(role, currentUser);
		} else {
			missingParameters.add("name");
			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

	}

	public void update(RoleDto postData, User currentUser)
			throws MeveoApiException {

		String name = postData.getName();
		if (name != null) {
			Role role = roleService.findByName(name);

			if (role == null) {
				throw new EntityDoesNotExistsException(Role.class, name, "name");
			}
			
			role.setDescription(postData.getDescription());
			
			List<PermissionDto> permissionDtos = postData.getPermission();			
			if (permissionDtos != null && !permissionDtos.isEmpty()) {
				List<Permission> permissions = new ArrayList<Permission>();
				
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

			roleService.updateAudit(role, currentUser);

		} else {
			missingParameters.add("name");
			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

	}

	public RoleDto find(String name) throws MeveoApiException {
		RoleDto roleDto = null;
		if (name != null) {
			Role role = roleService.findByName(name);
			if (role == null) {
				throw new EntityDoesNotExistsException(Role.class, name, "name");
			}
			roleDto = new RoleDto(role);
		}
		return roleDto;
	}

	public void remove(String name) throws MeveoApiException {

		if (name != null) {
			Role role = roleService.findByName(name);
			if (role == null) {
				throw new EntityDoesNotExistsException(Role.class, name, "name");
			}
			role.setPermissions(null);
			roleService.remove(role);
		}
	}

	public void createOrUpdate(RoleDto postData, User currentUser)
			throws MeveoApiException {

		String name = postData.getName();
		if (name != null) {
			Role role = roleService.findByName(name);
			if (role == null) {
				create(postData, currentUser);
			} else {
				update(postData, currentUser);
			}
		} else {
			missingParameters.add("name");
			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

}

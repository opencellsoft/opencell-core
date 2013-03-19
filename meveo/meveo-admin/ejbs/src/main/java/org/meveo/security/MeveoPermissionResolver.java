package org.meveo.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.permission.PermissionResolver;
import org.jboss.solder.logging.Logger;
import org.meveo.model.security.Role;

@Model
public class MeveoPermissionResolver implements Serializable, PermissionResolver {

	private static final long serialVersionUID = -7908760356168494113L;

	@Inject
	private Logger log;

	@Inject
	private Identity identity;

	private Map<String, Boolean> cachedPermissions = new HashMap<String, Boolean>();

	/*
	 * Check if user has given permission to a resource. Cache response
	 * 
	 * @see
	 * org.jboss.seam.security.permission.PermissionResolver#hasPermission(java
	 * .lang.Object, java.lang.String)
	 */
	public boolean hasPermission(Object resource, String permission) {

		if (!identity.isLoggedIn()) {
			return false;
		}

		String cacheKey = resource + "_" + permission;
		if (!cachedPermissions.containsKey(cacheKey)) {

			boolean has = false;
			if (((MeveoUser) identity.getUser()) != null
					&& ((MeveoUser) identity.getUser()).getUser() != null
					&& ((MeveoUser) identity.getUser()).getUser().getRoles() != null) {
				for (Role role : ((MeveoUser) identity.getUser()).getUser().getRoles()) {
					if (role.hasPermission(resource.toString(), permission)) {
						has = true;
						break;
					}
				}
			}
			cachedPermissions.put(cacheKey, has);
		}

		// log.error("AKK check has permission " + cacheKey + " " +
		// cachedPermissions.get(cacheKey));
		return cachedPermissions.get(cacheKey);

	}

	public void filterSetByAction(Set<Object> resources, String permission) {
	}
}

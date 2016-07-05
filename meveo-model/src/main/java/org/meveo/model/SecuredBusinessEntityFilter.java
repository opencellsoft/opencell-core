package org.meveo.model;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.slf4j.Logger;

/**
 * Implements filtering logic for a specific DTO.
 *
 * @author Tony Alejandro
 */
public abstract class SecuredBusinessEntityFilter {
	
	@Inject
	protected Logger log;
	
	public Class<? extends SecuredBusinessEntityFilter> getFilterClass(){
		return this.getClass();
	}
	
	public abstract Object filterResult(Object result, User user, Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap);
	
}

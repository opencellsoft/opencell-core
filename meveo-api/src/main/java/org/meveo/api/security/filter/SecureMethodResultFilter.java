package org.meveo.api.security.filter;

import javax.inject.Inject;

import org.meveo.model.admin.User;
import org.slf4j.Logger;

/**
 * Implements filtering logic for a specific DTO.
 *
 * @author Tony Alejandro
 */
public abstract class SecureMethodResultFilter {
	
	@Inject
	protected Logger log;
	
	public Class<? extends SecureMethodResultFilter> getFilterClass(){
		return this.getClass();
	}
	
	public abstract Object filterResult(Object result, User user);
	
}

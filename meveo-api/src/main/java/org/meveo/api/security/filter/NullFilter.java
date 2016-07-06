package org.meveo.api.security.filter;

import java.util.Map;
import java.util.Set;

import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;

public class NullFilter extends SecureMethodResultFilter {

	@Override
	public Object filterResult(Object result, User user, Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap) {
		return result;
	}

}

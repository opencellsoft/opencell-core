package org.meveo.model;

import java.util.Map;
import java.util.Set;

import org.meveo.model.SecuredBusinessEntityFilter;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;

public class NullSecuredBusinessEntityFilter extends SecuredBusinessEntityFilter {

	@Override
	public Object filterResult(Object result, User user, Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap) {
		return result;
	}

}

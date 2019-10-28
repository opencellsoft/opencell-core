package org.meveo.api.security.filter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.security.MeveoUser;

/**
 * This is the default result filter. I does not do any filtering. It is used if
 * a resultFilter attribute is not defined in the
 * {@link SecuredBusinessEntityMethod}. i.e. when the method result does not
 * need to be filtered.
 * 
 * @author Tony Alejandro
 *
 */
public class NullFilter extends SecureMethodResultFilter {

	@Override
	public Object filterResult(Method methodContext, Object result, MeveoUser currentUser, Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap) {
		return result;
	}

}

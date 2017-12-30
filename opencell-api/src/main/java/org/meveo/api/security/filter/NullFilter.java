package org.meveo.api.security.filter;

import java.lang.reflect.Method;

import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.model.admin.User;
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
	public Object filterResult(Method methodContext, Object result, MeveoUser currentUser, User user) {
		return result;
	}

}

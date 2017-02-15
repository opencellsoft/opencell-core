package org.meveo.api.security.filter;

import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;

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
	public Object filterResult(Object result) {
		return result;
	}

}

package org.meveo.api.security.Interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.api.security.filter.NullFilter;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.api.security.parameter.SecureMethodParameter;

/**
 * Identifies API methods that require proper user permissions to access.
 *
 * @author Tony Alejandro
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface SecuredBusinessEntityMethod {

	SecureMethodParameter[] validate();

	SecureMethodParameter user();

	Class<? extends SecureMethodResultFilter> resultFilter() default NullFilter.class;
}

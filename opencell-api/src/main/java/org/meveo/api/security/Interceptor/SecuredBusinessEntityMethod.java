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

    /**
     * Contains an array of {@link SecureMethodParameter} annotations that describe how the method parameters are going to be validated.
     * 
     * @return Array off secure method parameter
     */
    SecureMethodParameter[] validate() default {};

    /**
     * The result filter class that will be used to filter the results for entities that should be accessible to the user.
     * 
     * @return The secure method result filter
     */
    Class<? extends SecureMethodResultFilter> resultFilter() default NullFilter.class;
}

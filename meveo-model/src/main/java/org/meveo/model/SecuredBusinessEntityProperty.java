package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies DTO properties that require proper user permissions to access.
 *
 * @author Tony Alejandro
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface SecuredBusinessEntityProperty {
    SBEParam[] parameters();
    
    Class<? extends BusinessEntity> entityClass();
    
    Class<? extends SecuredBusinessEntityFilter> filterClass() default NullSecuredBusinessEntityFilter.class;
}

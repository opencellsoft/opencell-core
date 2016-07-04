package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.ObjectUtils.Null;

/**
 * Identifies DTO properties that require proper user permissions to access.
 *
 * @author Tony Alejandro
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface SecuredBusinessEntityProperty {
    String property() default "";
    Class<?> dtoClass() default Null.class;
    Class<? extends BusinessEntity> entityClass();
}

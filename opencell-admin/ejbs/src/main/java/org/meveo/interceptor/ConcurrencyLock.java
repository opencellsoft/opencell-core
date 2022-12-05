package org.meveo.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.interceptor.InterceptorBinding;

/**
 * Annotation to bind ConcurrencyLockInterceptor interceptor to apply concurrency lock based on Long parameter value
 * 
 * @author Andrius Karpavicius
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface ConcurrencyLock {

    /**
     * This attribute is used to indicate the index of a method parameter that will be used as a lock value. Parameter can point to Long type parameter or an IEntity, in which case an entitie's ID value will be used. In
     * a case when a parameter point to some other data type of value is NULL, no lock will be applied.
     * 
     * e.g. if we annotate a method that was defined as:
     * 
     * {@code someMethod(Long param1, IEntity param2)}
     * 
     * Then lockParameter value of 0 will refer to {@code param1} and value 1 will refer to {@code param2}.
     * 
     * @return The index of the parameter.
     */
    int lockParameter() default 0;
}
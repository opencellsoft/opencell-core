package org.meveo.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * Annotation to bind interceptor that in case of application managed persistence context (GUI and secodary tenants. See EntityManagerProvider.getEntityManager()), a new EM will be
 * instantiated for the period of a method call
 * 
 * @author Andrius Karpavicius
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface JpaAmpNewTx {

}
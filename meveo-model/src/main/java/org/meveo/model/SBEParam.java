package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * This contains data on how to retrieve the parameters of a
 * {@link SecuredBusinessEntityProperty} annotated method.
 * 
 * @author Tony Alejandro
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface SBEParam {

	String property() default "";

	Class<?> dataClass() default String.class;

	int index() default 0;

	SBEParamType type();
}

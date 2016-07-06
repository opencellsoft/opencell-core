package org.meveo.api.security.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;

/**
 * 
 * This contains data on how to retrieve the parameters of a
 * {@link SecuredBusinessEntityMethod} annotated method.
 * 
 * @author Tony Alejandro
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface SecureMethodParameter {
	int index() default 0;

	String property() default "";

	Class<? extends BusinessEntity> entity() default Seller.class;

	Class<? extends SecureMethodParameterParser<?>> parser() default CodeParser.class;

}

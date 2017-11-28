package org.meveo.api.security.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.model.BusinessEntity;

/**
 * 
 * This contains data on how to retrieve the parameters of a {@link SecuredBusinessEntityMethod} annotated method.
 * 
 * @author Tony Alejandro
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface SecureMethodParameter {
    /**
     * This attribute is used to indicate the index of a method parameter that is targeted by this annotation.
     * 
     * e.g. if we annotate a method that was defined as:
     * 
     * {@code someMethod(ObjectType1 param1, ObjectType2 param2)}
     * 
     * Then index 0 will refer to {@code param1} and index 1 will refer to {@code param2}.
     * 
     * @return The index of the parameter.
     */
    int index() default 0;

    /**
     * The property attribute refers to a property name of the parameter object.
     * 
     * e.g. if we annotate a method that was defined as:
     * 
     * {@code someMethod(ObjectType1 param1)}
     * 
     * Then define a property of {@code code} then during validation, the value of {@code param1.code} will be evaluated.
     * 
     * @return The property name of the value to be retrieved/evaluated.
     */
    String property() default "";

    /**
     * The entity attribute refers to the entity class that will be created from the extracted data. An example for its use is if the parameter we are receiving just contains the
     * code of a {@link BusinessEntity}. A new instance of this entity class is created and then the code will be assigned to it.
     * 
     * @return The entity class that will be instantiated.
     */
    Class<? extends BusinessEntity> entityClass();

    /**
     * The parser attribute defines the parser implementation that will be used to process the parameter. See {@link SecureMethodParameterParser} for more information.
     * 
     * @return The parser implementation that will be used to process the parameter.
     */
    Class<? extends SecureMethodParameterParser<?>> parser() default CodeParser.class;

}

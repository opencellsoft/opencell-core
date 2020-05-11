/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.security.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.parameter.CodeParser;
import org.meveo.api.security.parameter.SecureMethodParameterParser;
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

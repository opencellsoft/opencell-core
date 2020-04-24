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

import org.meveo.model.BusinessEntity;

/**
 * Identifies the filtering rule to apply to items selected for filtering
 * 
 * Specifies how to reconstruct an object used to compare what user has access to. Used in conjunction with {@link FilterResults}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface FilterProperty {

    /**
     * Name of a property of item selected for filtering. The value will be used to reconstruct an object of a given entity class
     * 
     * @return name of property.
     */
    String property();

    /**
     * Identifies the entity type that property value corresponds to. e.g. if CustomerAccount.class is passed into this attribute, then property value resolved from a "property"
     * will correspond to code field of a CustomerAccount object.
     * 
     * @return business entity class.
     */
    Class<? extends BusinessEntity> entityClass();

    /**
     * Shall access to an entity be granted in cases when property is resolved to a null value. If set to True, user will have access to entities that match his security settings
     * and those that have no property value set.
     * 
     * @return true/false
     */
    boolean allowAccessIfNull() default false;
}
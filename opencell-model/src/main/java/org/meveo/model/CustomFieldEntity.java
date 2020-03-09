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

package org.meveo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author phung
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface CustomFieldEntity {

    /**
     * (Required) A custom field template code prefix.
     * 
     * @return cft code prefix
     */
    String cftCodePrefix() default "";

    /**
     * Additional fields of an entity that should be included to complete a custom field template code value.
     * 
     * @return array of CFT code fields.
     */
    String[] cftCodeFields() default {};

    /**
     * Setting to true will allow the entity to be shown in the Custom Entities page.
     * 
     * @return true/false
     */
    boolean isManuallyManaged() default true;

    /**
     * Parent custom field entity (field names) in case custom field values should be inherited from a parent entity
     * 
     * @return Array of entity field names
     */
    String[] inheritCFValuesFrom() default {};

    /**
     * Should custom field values be inherited from a provider
     * 
     * @return True if value should be inherited
     */
    boolean inheritFromProvider() default false;
}
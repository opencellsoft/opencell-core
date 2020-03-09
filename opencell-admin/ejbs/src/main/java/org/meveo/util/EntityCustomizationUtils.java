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

package org.meveo.util;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.job.Job;

public class EntityCustomizationUtils {

    /**
     * Determine appliesTo value for custom field templates, actions, etc..
     * 
     * @param clazz Class customization applies to
     * @param code Entity code (applies to CustomEntityTemplate only)
     * @return An "appliesTo" value for a given class
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String getAppliesTo(Class clazz, String code) {

        String appliesToPrefix = null;
        if (Job.class.isAssignableFrom(clazz)) {
            appliesToPrefix = Job.CFT_PREFIX + "_" + ReflectionUtils.getCleanClassName(clazz.getSimpleName());

        } else if (CustomEntityTemplate.class.isAssignableFrom(clazz)) {
            appliesToPrefix = CustomEntityTemplate.getAppliesTo(code);

        } else {
            appliesToPrefix = ((CustomFieldEntity) clazz.getAnnotation(CustomFieldEntity.class)).cftCodePrefix();
        }

        return appliesToPrefix;
    }

    /**
     * Get entity code from applies to value. Applicable to CustomEntityTempalate/Instance only
     * 
     * @param appliesTo An "appliesTo" value
     * @return Entity code part of "appliesTo" value
     */
    public static String getEntityCode(String appliesTo) {
        int pos = appliesTo.indexOf("_");
        if (pos > 0) {
            return appliesTo.substring(pos + 1);
        } else {
            return null;
        }
    }
}
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

package org.meveo.api.security.filter;

import java.util.*;

import javax.inject.Inject;

import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.config.FilterPropertyConfig;
import org.meveo.api.security.config.FilterResultsConfig;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.security.MeveoUser;
import org.meveo.service.security.SecuredBusinessEntityService;

public class ObjectFilter extends SecureMethodResultFilter {

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object filterResult(FilterResultsConfig filterResultsConfig, Object result, MeveoUser currentUser, Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap) throws MeveoApiException {
        if (result == null) {
            // result is empty. no need to filter.
            log.warn("Result is empty. Skipping filter...");
            return result;
        }

        // Result is not annotated for filtering,
        if (filterResultsConfig == null) {
            return result;
        }

        boolean allowAccess = false;
        Object itemToFilter = result;

        // Various property filters are connected by OR - any filter match will consider item as a valid one
        filterLoop: for (FilterPropertyConfig propertyConfig : filterResultsConfig.getItemPropertiesToFilter()) {
            try {

                Collection resolvedValues = new ArrayList<>();
                Object resolvedValue = ReflectionUtils.getPropertyValue(itemToFilter, propertyConfig.getProperty());
                if (resolvedValue == null) {
                    if (propertyConfig.isAllowAccessIfNull()) {
                        log.debug("Adding item {} to filtered list.", itemToFilter);
                        allowAccess = true;
                    } else {
                        log.debug("Property " + propertyConfig.getProperty() + " on item to filter " + itemToFilter + " was resolved to null. Entity will be filtered out");
                    }
                    continue;

                } else if (resolvedValue instanceof Collection) {
                    resolvedValues = (Collection) resolvedValue;

                } else {
                    resolvedValues = new ArrayList<>();
                    resolvedValues.add(resolvedValue);
                }

                for (Object value : resolvedValues) {

                    if (value == null) {
                        continue;
                    }

                    BusinessEntity entity = propertyConfig.getEntityClass().newInstance();
                    entity.setCode((String) value);// FilterProperty could be expanded to include a target property to set instead of using "code"

                    if (securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false)) {
                        log.debug("Adding item {} to filtered list.", entity);
                        allowAccess = true;
                        break filterLoop;
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InvalidParameterException("Failed to create new instance of: " + propertyConfig.getEntityClass());
            }
        }

        if (!allowAccess) {
            throw new AccessDeniedException();
        }
        return result;
    }
}
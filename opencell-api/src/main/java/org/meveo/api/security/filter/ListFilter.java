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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.dto.response.SearchResponse;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.config.FilterPropertyConfig;
import org.meveo.api.security.config.FilterResultsConfig;
import org.meveo.api.security.config.SecureMethodParameterConfig;
import org.meveo.api.security.parameter.ObjectPropertyParser;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.security.MeveoUser;
import org.meveo.service.security.SecuredBusinessEntityService;

import javax.inject.Inject;
import java.util.*;

public class ListFilter extends SecureMethodResultFilter {

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

        List filteredList = new ArrayList<>();
        List itemsToFilter = null;

        try {
            itemsToFilter = (List) getItemsForFiltering(result, filterResultsConfig.getPropertyToFilter());

            // Nothing found to filter
            if (itemsToFilter == null || itemsToFilter.isEmpty()) {
                return result;
            }
        } catch (IllegalAccessException e) {
            throw new InvalidParameterException(String.format("Failed to retrieve property: %s of DTO %s.", filterResultsConfig.getPropertyToFilter(), result));
        }

        for (Object itemToFilter : itemsToFilter) {
            // Various property filters are connected by OR - any filter match will consider item as a valid one
            filterLoop: for (FilterPropertyConfig filterPropertyConfig : filterResultsConfig.getItemPropertiesToFilter()) {
                try {

                    Collection resolvedValues = new ArrayList<>();
                    Object resolvedValue = ReflectionUtils.getPropertyValue(itemToFilter, filterPropertyConfig.getProperty());
                    if (resolvedValue == null) {
                        if (filterPropertyConfig.isAllowAccessIfNull()) {
                            log.debug("Adding item {} to filtered list.", itemToFilter);
                            filteredList.add(itemToFilter);
                        } else {
                            log.debug("Property " + filterPropertyConfig.getProperty() + " on item to filter " + itemToFilter + " was resolved to null. Entity will be filtered out");
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

                        BusinessEntity entity = filterPropertyConfig.getEntityClass().newInstance();
                        entity.setCode((String) value);// FilterProperty could be expanded to include a target property to set instead of using "code"

                        if (securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false)) {
                            log.debug("Adding item {} to filtered list.", entity);
                            filteredList.add(itemToFilter);
                            break filterLoop;
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new InvalidParameterException("Failed to create new instance of: " + filterPropertyConfig.getEntityClass());
                }
            }
        }

        itemsToFilter.clear();
        itemsToFilter.addAll(filteredList);
        if(result instanceof SearchResponse) {
        	SearchResponse response = (SearchResponse)result;
        	int totalRecords = filteredList.size();
			response.getPaging().setTotalNumberOfRecords(totalRecords);
        	String totalRecordsPropertyName = filterResultsConfig.getTotalRecords();
			if (!totalRecordsPropertyName.isEmpty()) {
				try {
					updateItemsCount(response, totalRecordsPropertyName, totalRecords);
				} catch (IllegalAccessException e) {
					throw new InvalidParameterException(String.format("Failed to update property: %s of DTO %s.",
							totalRecordsPropertyName, result));
				}
			}
        	return response;
        }

        return result;
    }

    /**
     * This is a recursive function that aims to walk through the properties of an object until it gets the final value.
     * 
     * e.g. If we received an Object named obj and given a string property of code.name, then the value of obj.code.name will be returned.
     * 
     * Logic is the same as {@link ObjectPropertyParser#getParameterValue(SecureMethodParameterConfig, Object[])}
     * 
     * @param obj The object that contains the property value.
     * @param property The property of the object that contains the data.
     * @return The value of the data contained in obj.property
     * @throws IllegalAccessException
     */
    private Object getItemsForFiltering(Object obj, String property) throws IllegalAccessException {
        int fieldIndex = property.indexOf(".");
        if (fieldIndex == -1) {
            return FieldUtils.readField(obj, property, true);
        }
        String fieldName = property.substring(0, fieldIndex);
        Object fieldValue = FieldUtils.readField(obj, fieldName, true);
        return getItemsForFiltering(fieldValue, property.substring(fieldIndex + 1));
    }
    
    private void updateItemsCount(Object obj, String property, int totalRecords) throws IllegalAccessException {
        int fieldIndex = property.indexOf(".");
        if (fieldIndex == -1) {
        	 Class<?> type = FieldUtils.getField(obj.getClass(), property, true).getType();
        	 if(type.equals(Long.class)) {
				FieldUtils.writeField(obj, property, new Long(totalRecords), true);
        	 }else {
        		 FieldUtils.writeField(obj, property, totalRecords, true);
        	 }
             return;
        }
        String fieldName = property.substring(0, fieldIndex);
        Object fieldValue = FieldUtils.readField(obj, fieldName, true);
        updateItemsCount(fieldValue, property.substring(fieldIndex + 1), totalRecords);
    }

}

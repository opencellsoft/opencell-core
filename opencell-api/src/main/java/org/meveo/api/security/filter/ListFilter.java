package org.meveo.api.security.filter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.parameter.ObjectPropertyParser;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.security.MeveoUser;
import org.meveo.service.security.SecuredBusinessEntityService;

public class ListFilter extends SecureMethodResultFilter {

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object filterResult(Method methodContext, Object result, MeveoUser currentUser, User user) throws MeveoApiException {
        if (result == null) {
            // result is empty. no need to filter.
            log.warn("Result is empty. Skipping filter...");
            return result;
        }

        FilterResults filterResults = methodContext.getAnnotation(FilterResults.class);

        // Result is not annotated for filtering,
        if (filterResults == null) {
            return result;
        }

        List filteredList = new ArrayList<>();
        List itemsToFilter = null;

        try {
            itemsToFilter = (List) getItemsForFiltering(result, filterResults.propertyToFilter());

            // Nothing found to filter
            if (itemsToFilter == null || itemsToFilter.isEmpty()) {
                return result;
            }
        } catch (IllegalAccessException e) {
            throw new InvalidParameterException(String.format("Failed to retrieve property: %s of DTO %s.", filterResults.propertyToFilter(), result));
        }

        for (Object itemToFilter : itemsToFilter) {
            // Various property filters are connected by OR - any filter match will consider item as a valid one
            filterLoop: for (FilterProperty filterProperty : filterResults.itemPropertiesToFilter()) {
                try {

                    Collection resolvedValues = new ArrayList<>();
                    Object resolvedValue = ReflectionUtils.getPropertyValue(itemToFilter, filterProperty.property());
                    if (resolvedValue == null) {
                        if (filterProperty.allowAccessIfNull()) {
                            log.debug("Adding item {} to filtered list.", itemToFilter);
                            filteredList.add(itemToFilter);
                        } else {
                            log.debug("Property " + filterProperty.property() + " on item to filter " + itemToFilter + " was resolved to null. Entity will be filtered out");
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

                        BusinessEntity entity = filterProperty.entityClass().newInstance();
                        entity.setCode((String) value);// FilterProperty could be expanded to include a target property to set instead of using "code"

                        if (securedBusinessEntityService.isEntityAllowed(entity, user, false)) {
                            log.debug("Adding item {} to filtered list.", entity);
                            filteredList.add(itemToFilter);
                            break filterLoop;
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new InvalidParameterException("Failed to create new instance of: " + filterProperty.entityClass());
                }
            }
        }

        itemsToFilter.clear();
        itemsToFilter.addAll(filteredList);

        return result;
    }

    /**
     * This is a recursive function that aims to walk through the properties of an object until it gets the final value.
     * 
     * e.g. If we received an Object named obj and given a string property of code.name, then the value of obj.code.name will be returned.
     * 
     * Logic is the same as {@link ObjectPropertyParser#getPropertyValue()}
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

}

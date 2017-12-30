package org.meveo.api.security.filter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.security.MeveoUser;
import org.meveo.service.security.SecuredBusinessEntityService;

public class ObjectFilter extends SecureMethodResultFilter {

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

        boolean allowAccess = false;
        Object itemToFilter = result;

        // Various property filters are connected by OR - any filter match will consider item as a valid one
        filterLoop: for (FilterProperty filterProperty : filterResults.itemPropertiesToFilter()) {
            try {

                Collection resolvedValues = new ArrayList<>();
                Object resolvedValue = ReflectionUtils.getPropertyValue(itemToFilter, filterProperty.property());
                if (resolvedValue == null) {
                    if (filterProperty.allowAccessIfNull()) {
                        log.debug("Adding item {} to filtered list.", itemToFilter);
                        allowAccess = true;
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
                        allowAccess = true;
                        break filterLoop;
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InvalidParameterException("Failed to create new instance of: " + filterProperty.entityClass());
            }
        }

        if (!allowAccess) {
            throw new AccessDeniedException();
        }
        return result;
    }
}
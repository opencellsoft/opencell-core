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

package org.meveo.api.security.Interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.security.config.FilterPropertyConfig;
import org.meveo.api.security.config.FilterResultsConfig;
import org.meveo.api.security.config.SecureMethodParameterConfig;
import org.meveo.api.security.config.SecuredBusinessEntityConfig;
import org.meveo.api.security.config.SecuredBusinessEntityConfigFactory;
import org.meveo.api.security.config.SecuredMethodConfig;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityAnnotationConfigFactory;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.filter.ObjectFilter;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.api.security.filter.SecureMethodResultFilterFactory;
import org.meveo.api.security.parameter.SecureMethodParameterHandler;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.security.Role;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This will handle the processing of {@link SecuredBusinessEntityMethod} annotated methods.
 *
 * @author Tony Alejandro
 * @author Wassim Drira
 * @author mohamed stitane
 * @author Mounir Boukayoua
 * @lastModifiedVersion 5.0
 */
public class SecuredBusinessEntityMethodInterceptor implements Serializable {

    private static final long serialVersionUID = 4656634337151866255L;

    private static final Logger log = LoggerFactory.getLogger(SecuredBusinessEntityMethodInterceptor.class);

    private static final String AND_OR_FIELD_SUFFIX = "_secured";

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    @Inject
    private SecureMethodResultFilterFactory filterFactory;

    @Inject
    private SecureMethodParameterHandler parameterHandler;

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    /**
     * Default one which is injected is {@link SecuredBusinessEntityAnnotationConfigFactory}
     */
    @Inject
    protected SecuredBusinessEntityConfigFactory securedBusinessEntityConfigFactory;

    /**
     * This is called before a method that makes use of the {@link SecuredBusinessEntityMethodInterceptor} is called. It contains logic on retrieving the attributes of the {@link SecuredBusinessEntityMethod} annotation
     * placed on the method and then validate the parameters described in the {@link SecureMethodParameter} validation attributes and then filters the result using the {@link SecureMethodResultFilter} filter attribute.
     *
     * @param context The invocation context
     * @return The filtered result object
     * @throws Exception exception
     */
    @AroundInvoke
    public Object aroundInvoke(InvocationContext context) throws Exception {
        // Check if secured entities are enabled.
        boolean secureEntitesEnabled = paramBeanFactory.getInstance().getPropertyAsBoolean("secured.entities.enabled", true);
        if (!secureEntitesEnabled) {
            return context.proceed();
        }

        SecuredBusinessEntityConfig sbeConfig = this.securedBusinessEntityConfigFactory.get(context);
        return checkForSecuredEntities(context, sbeConfig);
    }

    /**
     * Check an API method for configured secured entities
     *
     * @param context method invocation context
     * @param sbeConfig {@link SecuredBusinessEntityConfig} instance
     * @return the method result if check is success
     * @throws Exception exception if check is failed
     */
    protected Object checkForSecuredEntities(InvocationContext context, SecuredBusinessEntityConfig sbeConfig) throws Exception {
        if (sbeConfig == null || sbeConfig.getSecuredMethodConfig() == null) {
            return context.proceed();
        }

        Map<Class<?>, Set<SecuredEntity>> allSecuredEntitiesMap = getAllSecuredEntities(currentUser);
        boolean hasRestrictions = !allSecuredEntitiesMap.isEmpty();
        if (!hasRestrictions) {
            return context.proceed();
        }

        Class<?> objectClass = context.getMethod().getDeclaringClass();
        String objectName = objectClass.getSimpleName();
        String methodName = context.getMethod().getName();

        log.debug("Checking method {}.{} for secured BusinessEntities", objectName, methodName);

        Object[] values = context.getParameters();

        SecuredMethodConfig securedMethodConfig = sbeConfig.getSecuredMethodConfig();

        if (securedMethodConfig.getResultFilter().equals(ListFilter.class)) {
            List<SecuredEntity> securedEntities = allSecuredEntitiesMap.values().stream().flatMap(Set::stream).collect(Collectors.toList());
            addSecuredEntitiesToFilters(securedEntities, values, sbeConfig.getFilterResultsConfig());
        }

        // check validation
        SecureMethodParameterConfig[] parametersForValidation = securedMethodConfig.getValidate();
        if (parametersForValidation != null) {
            for (SecureMethodParameterConfig parameterConfig : parametersForValidation) {
                List<BusinessEntity> entities = parameterHandler.getParameterValue(parameterConfig, values, BusinessEntity.class);
                if (CollectionUtils.isNotEmpty(entities)) {
                    boolean isAllowed = false;
                    for (BusinessEntity entity : entities) {
                        log.debug("Checking if entity={} is allowed for currentUser", entity);
                        if (entityIsBeingCreated(methodName, entity)) {
                            log.debug("New entity is being created by calling createOrUpdate(). Check is OK");
                            isAllowed =true;
                            break;
                        }
                        if (entity != null && securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false)) {
                            log.debug("Checked entity is OK");
                            isAllowed = true;
                            break;
                        }
                    }
                    if (!isAllowed) {
                        throw new AccessDeniedException("Access to entity details is not allowed.");
                    }
                }
            }
        }
        log.debug("Allowing method {}.{} to be invoked.", objectName, methodName);
        Object result = context.proceed();

        // Perform results filtering if it was not done as part of SQl query (exclude ListFilter and NullFilter (as it does nothing))
        if (securedMethodConfig.getResultFilter().equals(ObjectFilter.class)) {
            SecureMethodResultFilter resultsFilter = filterFactory.getFilter(securedMethodConfig.getResultFilter());
            log.debug("Method {}.{} results will be filtered using {} filter.", objectName, methodName, resultsFilter);
            result = resultsFilter.filterResult(sbeConfig.getFilterResultsConfig(), result, currentUser, allSecuredEntitiesMap);
        }
        return result;
    }

    /**
     * Checking if the entity to validate is not about to be created by
     * calling createOrUpdate on the intercepted API
     * @param methodName method name intercepted
     * @param entity to be validated by SecuredEntities check
     * @return if the entity is being created or not
     */
    private boolean entityIsBeingCreated(String methodName, BusinessEntity entity) {
        if ("createOrUpdate".equals(methodName) && entity.getId() == null && entity.getCode() != null) {
            try {
                Object entityFound = userService.getEntityManager()
                        .createQuery("select e from " + entity.getClass().getSimpleName() + " e where lower(code)=:code")
                        .setParameter("code", entity.getCode().toLowerCase())
                        .setMaxResults(1).getSingleResult();
                if (entityFound != null) {
                    return false;
                }
            } catch (NoResultException e) {
                return true;
            } catch (Exception e) {
                //If query couldn't be executed then return false
                // to continue SecuredEntities checking
                return false;
            }
        }
        return false;
    }

    /**
     * Get all accessible entities for the current user, both associated directly to the user or to its associated roles. Those accessible entities are then grouped by types into Map
     *
     * @param currentUser MeveoUser current user
     * @return current user's accessible entities
     */
    private Map<Class<?>, Set<SecuredEntity>> getAllSecuredEntities(MeveoUser currentUser) {
        List<SecuredEntity> allSecuredEntities = new ArrayList<>();
        User user = userService.findByUsername(currentUser.getUserName());
        allSecuredEntities.addAll(user.getSecuredEntities().stream().filter(securedEntity -> !securedEntity.getDisabledAsBoolean()).collect(Collectors.toList()));

        List<Role> rolesWithSecuredEntities = roleService.getEntityManager().createNamedQuery("Role.getRolesWithSecuredEntities", Role.class).setParameter("currentUserRoles", currentUser.getRoles()).getResultList();
        allSecuredEntities.addAll(rolesWithSecuredEntities.stream().map(Role::getSecuredEntities).flatMap(List::stream).filter(securedEntity -> !securedEntity.getDisabledAsBoolean()).collect(Collectors.toList()));

        // group secured entites by types into Map
        Map<Class<?>, Set<SecuredEntity>> securedEntitiesMap = new HashMap<>();
        Set<SecuredEntity> securedEntitySet = null;
        try {
            for (SecuredEntity securedEntity : allSecuredEntities) {
                Class<?> securedBusinessEntityClass = Class.forName(securedEntity.getEntityClass());
                if (securedEntitiesMap.get(securedBusinessEntityClass) == null) {
                    securedEntitySet = new HashSet<>();
                    securedEntitiesMap.put(securedBusinessEntityClass, securedEntitySet);
                }
                securedEntitiesMap.get(securedBusinessEntityClass).add(securedEntity);
            }
        } catch (ClassNotFoundException e) {
            // do nothing
        }
        return securedEntitiesMap;
    }

    /**
     * Adding a secured entities code to the filters for paging
     *
     * @param securedEntities all secured entities
     * @param values the context parameter
     * @param filterResultsConfig Results filter configuration
     */
    private void addSecuredEntitiesToFilters(List<SecuredEntity> securedEntities, Object[] values, FilterResultsConfig filterResultsConfig) {

        for (Object obj : values) {
            // Search filters are in PagingAndFiltering
            if (obj instanceof PagingAndFiltering) {
                PagingAndFiltering pagingAndFiltering = (PagingAndFiltering) obj;
                Map<String, Object> filters = Optional.ofNullable(pagingAndFiltering.getFilters()).orElse(new HashMap<>());
                updateFilters(securedEntities, filters, filterResultsConfig);
                pagingAndFiltering.setFilters(filters);
                break;

                // Search filters are in PaginationConfiguration
            } else if (obj instanceof PaginationConfiguration) {
                PaginationConfiguration paginationConfiguration = (PaginationConfiguration) obj;
                Map<String, Object> filters = Optional.ofNullable(paginationConfiguration.getFilters()).orElse(new HashMap<>());
                updateFilters(securedEntities, filters, filterResultsConfig);
                paginationConfiguration.setFilters(filters);
                break;
            }
        }
    }

    /**
     * Adding a secured entities code to the filters
     *
     * @param securedEntities a secured entities
     * @param filters filters to update
     * @param filterResultsConfig Filter result configuration
     */
    private Map<String, Object> updateFilters(List<SecuredEntity> securedEntities, Map<String, Object> filters, FilterResultsConfig filterResultsConfig) {

        if (filterResultsConfig.getItemPropertiesToFilter().length == 0) {
            return filters;
        }

        Map<String, List<String>> newFilterCriteria = new HashMap<String, List<String>>();
        for (FilterPropertyConfig propertyToFilter : filterResultsConfig.getItemPropertiesToFilter()) {

            String fieldName = propertyToFilter.getProperty();

            String propertyToFilterClass = propertyToFilter.getEntityClass().getSimpleName();

            for (SecuredEntity securedEntity : securedEntities) {
                String entityClass = securedEntity.getEntityClass();
                if (entityClass.lastIndexOf('.') > 0) {
                    entityClass = entityClass.substring(entityClass.lastIndexOf('.') + 1);
                }

                String propertyToFilterPath = getCriteriaPath(fieldName, propertyToFilterClass, entityClass);
                if (propertyToFilterPath == null) {
                    continue;
                }

                if (newFilterCriteria.containsKey(propertyToFilterPath)) {
                    newFilterCriteria.get(propertyToFilterPath).add(securedEntity.getCode());
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(securedEntity.getCode());
                    newFilterCriteria.put(propertyToFilterPath, list);
                }
            }

            // Add IS NULL as a possible value if field is optional
            if (propertyToFilter.isAllowAccessIfNull()) {
                List<String> list = new ArrayList<>();
                list.add(PersistenceService.SEARCH_IS_NULL);
                newFilterCriteria.put(PersistenceService.SEARCH_AND + AND_OR_FIELD_SUFFIX + " " + fieldName, list);
            }
        }

        // Was not able to apply any allowed entity as filtering criteria. A case when user is granted access to user account, but a higher resource (e.g. seller) is being accessed
        if (newFilterCriteria.isEmpty()) {
            throw new AccessDeniedException("Access to entity list is not allowed.");
        }

        boolean singleCriteria = newFilterCriteria.size() == 1;

        Map<String, Object> orFilterItems = new HashMap<String, Object>();

        for (Entry<String, List<String>> filterInfo : newFilterCriteria.entrySet()) {

            String propertyToFilterPath = filterInfo.getKey();
            String propertyToFilterPathAsList = "inList " + propertyToFilterPath;
            List<String> allowedValues = filterInfo.getValue();

            log.debug("Adding an access limit filter for {} field by {}", propertyToFilterPath, filterInfo.getValue());

            // A search is already done by that field - verify if it is a permitted value according to the secured entities
            if (singleCriteria && filters.containsKey(propertyToFilterPath)) {
                final Object initialValue = filters.get(propertyToFilterPath);
                if (!allowedValues.contains(initialValue)) {
                    throw new AccessDeniedException("Search for '" + propertyToFilterPath + "' by " + initialValue + " is not allowed.");
                }

                // A search is already done by a list of values - remove what is not permitted and throw AccessDeniedException if all values were eliminated
            } else if (singleCriteria && filters.containsKey(propertyToFilterPathAsList)) {
                final Object initialValue = filters.get(propertyToFilterPathAsList);
                List<String> initialValues = null;
                if (initialValue instanceof String) {
                    initialValues = new ArrayList(Arrays.asList(((String) initialValue).split(",")));

                } else {
                    initialValues = (List<String>) initialValue;
                }

                initialValues.retainAll(allowedValues);
                if (initialValues.size() == 0) {
                    throw new AccessDeniedException("Search for '" + propertyToFilterPathAsList + "' by " + initialValue + " is not allowed.");
                }

                filters.put(propertyToFilterPathAsList, initialValues);

                // In case of a single additional query filter to be added, if no current search for a given field, add it as a search criteria
            } else if (singleCriteria) {
                if (allowedValues.size() == 1) {
                    filters.put(propertyToFilterPath, allowedValues.get(0));
                } else {
                    filters.put(propertyToFilterPathAsList, allowedValues);
                }

                // // In case of a multiple additional query filters to be added, add it as a search criteria inside the OR clause
            } else {
                if (allowedValues.size() == 1) {
                    orFilterItems.put(propertyToFilterPath, allowedValues.get(0));
                } else {
                    orFilterItems.put(propertyToFilterPathAsList, allowedValues);
                }
            }
        }

        if (!orFilterItems.isEmpty()) {
            filters.put(PersistenceService.SEARCH_OR + AND_OR_FIELD_SUFFIX, orFilterItems);
        }
        return filters;

    }

    /**
     * Get a property hierarchy traversal path to filter one entity based on access right to another entity.
     *
     * @param propertyName Property to filter
     * @param tryToAccessEntityClass Class that corresponds to a property being filtered
     * @param allowedToAccessEntityClass Class that corresponds to an entity that user has access to
     * @return A property hierarchy traversal path. E.g. if user has access to Customer and a rule is applied on Subscription's userAccount property, a method would return
     *         "userAccount.billingAccount.customerAccount.customer.code"
     */
    private String getCriteriaPath(String propertyName, String tryToAccessEntityClass, String allowedToAccessEntityClass) {

        String[] classHierarchyByPosition = new String[] { "billingAccount", "customerAccount", "customer", "seller" };

        Map<String, Integer> classHierarchyByClass = Map.of("UserAccount", 0, "BillingAccount", 1, "CustomerAccount", 2, "Customer", 3, "Seller", 4);

        int posTryAccess = classHierarchyByClass.get(tryToAccessEntityClass);
        int posAllowed = classHierarchyByClass.get(allowedToAccessEntityClass);

        // User is allowed to access UserAccount, but tries to access Customer, it wont be permitted
        if (posAllowed < posTryAccess) {
            return null;

            // User is allowed to access Customer and is accessing a customer
        } else if (posAllowed == posTryAccess) {
            if (propertyName.equals("code")) {
                return "code";
            } else {
                return propertyName + ".code";
            }

            // User is allowed to access Customer and is accessing a userAccount - need to construct the whole hierarchy to climb up
        } else {

            // Check if seller is considered as parent entity
            if ("Seller".equals(allowedToAccessEntityClass) && !paramBeanFactory.getInstance().getPropertyAsBoolean("accessible.entity.allows.access.childs.seller", true)) {
                return null;
            }

            if (propertyName.equals("code")) {
                propertyName = null;
            }

            for (int i = posTryAccess; i < posAllowed; i++) {
                propertyName = (propertyName == null ? "" : propertyName + ".") + classHierarchyByPosition[i];
            }
            propertyName = propertyName + ".code";
            return propertyName;
        }

    }
}
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

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.security.config.SecureMethodParameterConfig;
import org.meveo.api.security.config.SecuredBusinessEntityConfig;
import org.meveo.api.security.config.SecuredBusinessEntityConfigFactory;
import org.meveo.api.security.config.SecuredMethodConfig;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityAnnotationConfigFactory;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.api.security.filter.SecureMethodResultFilterFactory;
import org.meveo.api.security.parameter.SecureMethodParameterHandler;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.security.Role;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
     * Default one which is injected
     * is {@link SecuredBusinessEntityAnnotationConfigFactory}
     */
    @Inject
    protected SecuredBusinessEntityConfigFactory securedBusinessEntityConfigFactory;

    /**
     * This is called before a method that makes use of the {@link SecuredBusinessEntityMethodInterceptor} is called. It contains logic on retrieving the attributes of the
     * {@link SecuredBusinessEntityMethod} annotation placed on the method and then validate the parameters described in the {@link SecureMethodParameter} validation attributes and
     * then filters the result using the {@link SecureMethodResultFilter} filter attribute.
     *
     * @param context  The invocation context
     * @return  The filtered result object
     * @throws Exception exception
     */
    @AroundInvoke
    public Object aroundInvoke(InvocationContext context) throws Exception {
        SecuredBusinessEntityConfig sbeConfig = this.securedBusinessEntityConfigFactory.get(context);
        return checkForSecuredEntities(context, sbeConfig);
    }

    /**
     * Check an API method for configured secured entities
     * @param context method invocation context
     * @param sbeConfig {@link SecuredBusinessEntityConfig} instance
     * @return the method result if check is success
     * @throws Exception exception if check is failed
     */
    protected Object checkForSecuredEntities(InvocationContext context, SecuredBusinessEntityConfig sbeConfig) throws Exception {
        if (sbeConfig == null || sbeConfig.getSecuredMethodConfig() == null ) {
            return context.proceed();
        }
        String secureSetting = paramBeanFactory.getInstance().getProperty("secured.entities.enabled", "true");
        boolean secureEntitesEnabled = Boolean.parseBoolean(secureSetting);
        // if not, immediately return.
        if (!secureEntitesEnabled) {
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
        //List<SecuredEntity> securedEntities = allSecuredEntitiesMap.values().stream().flatMap(Set::stream).collect(Collectors.toList());
        //addSecuredEntitiesToFilters(securedEntities, values);

        SecuredMethodConfig securedMethodConfig = sbeConfig.getSecuredMethodConfig();

        // check validation
        SecureMethodParameterConfig[] parametersForValidation = securedMethodConfig.getValidate();
        if (parametersForValidation != null) {
            for (SecureMethodParameterConfig parameterConfig : parametersForValidation) {
                List<BusinessEntity> entities = parameterHandler.getParameterValue(parameterConfig, values, BusinessEntity.class);
                if (CollectionUtils.isNotEmpty(entities)) {
                    boolean isAllowed = false;
                    for (BusinessEntity entity : entities) {
                        log.debug("Checking if entity={} is allowed for currentUser", entity);
                        if (entity != null && securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false)) {
                            log.debug("Checked entity is OK");
                            isAllowed =true;
                            break;
                        }
                    }
                    if(!isAllowed) {
                        throw new AccessDeniedException("Access to entity details is not allowed.");
                    }
                }
            }
        }
        log.debug("Allowing method {}.{} to be invoked.", objectName, methodName);
        Object result = context.proceed();

        // perform filtering
        SecureMethodResultFilter filter = filterFactory.getFilter(securedMethodConfig.getResultFilter());
        log.debug("Method {}.{} results will be filtered using {} filter.", objectName, methodName, filter);
        result = filter.filterResult(sbeConfig.getFilterResultsConfig(), result, currentUser, allSecuredEntitiesMap);
        return result;
    }

    /**
     * Get all accessible entities for the current user, both associated directly to the user
     * or to its associated roles.
     * Those accessible entities are then grouped by types into Map
     *
     * @param currentUser MeveoUser current user
     * @return current user's accessible entities
     */
    private Map<Class<?>, Set<SecuredEntity>> getAllSecuredEntities(MeveoUser currentUser) {
        List<SecuredEntity> allSecuredEntities = new ArrayList<>();
        User user = userService.findByUsername(currentUser.getUserName());
        allSecuredEntities.addAll(user.getSecuredEntities());

        List<Role> rolesWithSecuredEntities = roleService.getEntityManager().createNamedQuery("Role.getRolesWithSecuredEntities", Role.class)
                .setParameter("currentUserRoles", currentUser.getRoles())
                .getResultList();
        allSecuredEntities.addAll(rolesWithSecuredEntities.stream().map(Role::getSecuredEntities).flatMap(List::stream).collect(Collectors.toList()));


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
     * @param values          the context parameter
     */
    private void addSecuredEntitiesToFilters(List<SecuredEntity> securedEntities, Object[] values) {
        log.debug("Adding a secured entities code to the filters for paging");
        for (Object obj : values) {
            // Search filters are in PagingAndFiltering
            if (obj instanceof PagingAndFiltering) {
                PagingAndFiltering pagingAndFiltering = (PagingAndFiltering) obj;
                if (pagingAndFiltering.getLimit() != null && pagingAndFiltering.getOffset() != null) {
                    Map<String, Object> filters = Optional.ofNullable(pagingAndFiltering.getFilters()).orElse(new HashMap<>());
                    updateFilters(securedEntities, filters);
                    pagingAndFiltering.setFilters(filters);

                }
                break;
            }
            // Search filters are in PagingAndFiltering
            if (obj instanceof PaginationConfiguration) {
                PaginationConfiguration paginationConfiguration = (PaginationConfiguration) obj;
                if (paginationConfiguration.getNumberOfRows() != null && paginationConfiguration.getFirstRow() != null) {
                    Map<String, Object> filters = Optional.ofNullable(paginationConfiguration.getFilters()).orElse(new HashMap<>());
                    updateFilters(securedEntities, filters);
                    paginationConfiguration.setFilters(filters);
                }
                break;
            }
        }
    }

    /**
     * Adding a secured entities code to the filters
     *
     * @param securedEntities    a secured entities
     * @param filters filters to update
     */
    private Map<String, Object> updateFilters(List<SecuredEntity> securedEntities, Map<String, Object> filters) {
        for (SecuredEntity securedEntity : securedEntities) {
            final String entityClass = securedEntity.getEntityClass();
            //extract the field name from entity class, I supposed that the field name is the same as the Class name.
            String simpleClassName = entityClass.substring(entityClass.lastIndexOf('.') + 1);
            String fieldName = simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);
            log.debug("Code = {} for entity = {}", securedEntity.getCode(), fieldName);
            final String keyInList = "inList " + fieldName + ".code";
            if (filters.containsKey(fieldName)) {
                final Object initialValue = filters.get(fieldName);
                filters.put(keyInList, StringUtils.concat(initialValue, ",", securedEntity.getCode()));
                filters.remove(fieldName);
            } else if (filters.containsKey(keyInList)) {
                final Object initialList = filters.get(keyInList);
                filters.replace(keyInList, StringUtils.concat(initialList, ",", securedEntity.getCode()));
            } else {
                filters.put(fieldName + ".code", securedEntity.getCode());
            }
        }
        return filters;
    }
}

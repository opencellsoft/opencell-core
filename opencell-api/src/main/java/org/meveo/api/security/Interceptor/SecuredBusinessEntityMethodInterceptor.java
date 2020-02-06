package org.meveo.api.security.Interceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.api.security.filter.SecureMethodResultFilterFactory;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.api.security.parameter.SecureMethodParameterHandler;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This will handle the processing of {@link SecuredBusinessEntityMethod} annotated methods.
 *
 * @author Tony Alejandro
 * @author Wassim Drira
 * @author mohamed stitane
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

    /**
     * paramBean Factory allows to get application scope paramBean or provider specific paramBean
     */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    /**
     * This is called before a method that makes use of the {@link SecuredBusinessEntityMethodInterceptor} is called. It contains logic on retrieving the attributes of the
     * {@link SecuredBusinessEntityMethod} annotation placed on the method and then validate the parameters described in the {@link SecureMethodParameter} validation attributes and
     * then filters the result using the {@link SecureMethodResultFilter} filter attribute.
     *
     * @param context The invocation context
     * @return The filtered result object
     * @throws Exception exception
     */
    @AroundInvoke
    public Object checkForSecuredEntities(InvocationContext context) throws Exception {

        SecuredBusinessEntityMethod annotation = context.getMethod().getAnnotation(SecuredBusinessEntityMethod.class);
        if (annotation == null) {
            return context.proceed();
        }

        // log.error("AKK checking secured entities currentUser is {}", currentUser);

        // check if secured entities should be checked.
        String secureSetting = paramBeanFactory.getInstance().getProperty("secured.entities.enabled", "false");
        boolean secureEntitesEnabled = Boolean.parseBoolean(secureSetting);

        // if not, immediately return.
        if (!secureEntitesEnabled) {
            return context.proceed();
        }

        User user = userService.findByUsername(currentUser.getUserName());

        boolean hasRestrictions = user != null && user.getAllSecuredEntities() != null && !user.getAllSecuredEntities().isEmpty();

        if (!hasRestrictions) {
            return context.proceed();
        }

        Class<?> objectClass = context.getMethod().getDeclaringClass();
        String objectName = objectClass.getSimpleName();
        String methodName = context.getMethod().getName();

        log.debug("Checking method {}.{} for secured BusinessEntities", objectName, methodName);

        Object[] values = context.getParameters();

        addSecuredEntitiesToFilters(user.getSecuredEntities(), values);

        SecureMethodParameter[] parametersForValidation = annotation.validate();
        for (SecureMethodParameter parameter : parametersForValidation) {
            BusinessEntity entity = parameterHandler.getParameterValue(parameter, values, BusinessEntity.class);
            if (entity == null) {
                // TODO what to do if entity was not resolved because parameter value was null e.g. doing a search by a restricted field and dont provide any field value - that
                // means that instead of filtering search criteria, results should be filtered instead

            } else {
                if (!securedBusinessEntityService.isEntityAllowed(entity, user, false)) {
                    throw new AccessDeniedException("Access to entity details is not allowed.");
                }
            }
        }

        log.debug("Allowing method {}.{} to be invoked.", objectName, methodName);
        Object result = context.proceed();

        SecureMethodResultFilter filter = filterFactory.getFilter(annotation.resultFilter());
        log.debug("Method {}.{} results will be filtered using {} filter.", objectName, methodName, filter);
        result = filter.filterResult(context.getMethod(), result, currentUser, user);
        return result;

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
            if (obj instanceof PagingAndFiltering) {
                PagingAndFiltering pagingAndFiltering = (PagingAndFiltering) obj;
                updateFilters(securedEntities, pagingAndFiltering);
                break;
            }
        }
    }

    /**
     * Adding a secured entities code to the filters
     *
     * @param securedEntities    a secured entities
     * @param pagingAndFiltering a paging and filtering object
     */
    private void updateFilters(List<SecuredEntity> securedEntities, PagingAndFiltering pagingAndFiltering) {
        if (isNotNull(pagingAndFiltering)) {
            Map<String, Object> filters = Optional.ofNullable(pagingAndFiltering.getFilters()).orElse(new HashMap<>());
            for (SecuredEntity securedEntity : securedEntities) {
                final String entityClass = securedEntity.getEntityClass();
                //extract the field name from entity class, I supposed that the field name is the same as the Class name.
                final String fieldName = entityClass.substring(entityClass.lastIndexOf('.') + 1).toLowerCase();
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
                    filters.put(fieldName, securedEntity.getCode());
                }
            }
            pagingAndFiltering.setFilters(filters);
        }
    }

    /**
     * check if the object is null
     *
     * @param pagingAndFiltering a paging and filtering object
     * @return true or false
     */
    private boolean isNotNull(PagingAndFiltering pagingAndFiltering) {
        return pagingAndFiltering != null && pagingAndFiltering.getLimit() != null && pagingAndFiltering.getOffset() != null;
    }
}

package org.meveo.api.security.Interceptor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.Query;

import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.api.security.filter.SecureMethodResultFilterFactory;
import org.meveo.api.security.parameter.SecureMethodParameter;
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
import org.meveo.service.security.SecuredBusinessEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This will handle the processing of {@link SecuredBusinessEntityMethod} annotated methods.
 * 
 * @author Tony Alejandro
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
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
     * This is called before a method that makes use of the {@link SecuredBusinessEntityMethodInterceptor} is called. It contains logic on retrieving the attributes of the
     * {@link SecuredBusinessEntityMethod} annotation placed on the method and then validate the parameters described in the {@link SecureMethodParameter} validation attributes and
     * then filters the result using the {@link SecureMethodResultFilter} filter attribute.
     * 
     * @param context  The invocation context
     * @return  The filtered result object
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

        SecureMethodParameter[] parametersForValidation = annotation.validate();
        for (SecureMethodParameter parameter : parametersForValidation) {
            BusinessEntity entity = parameterHandler.getParameterValue(parameter, values, BusinessEntity.class);
            if (entity == null) {
                // TODO what to do if entity was not resolved because parameter value was null e.g. doing a search by a restricted field and dont provide any field value - that
                // means that instead of filtering search criteria, results should be filtered instead

            } else {
                if (!securedBusinessEntityService.isEntityAllowed(entity, allSecuredEntitiesMap, false)) {
                    throw new AccessDeniedException("Access to entity details is not allowed.");
                }
            }
        }

        log.debug("Allowing method {}.{} to be invoked.", objectName, methodName);
        Object result = context.proceed();

        SecureMethodResultFilter filter = filterFactory.getFilter(annotation.resultFilter());
        log.debug("Method {}.{} results will be filtered using {} filter.", objectName, methodName, filter);
        result = filter.filterResult(context.getMethod(), result, currentUser, allSecuredEntitiesMap);
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
}

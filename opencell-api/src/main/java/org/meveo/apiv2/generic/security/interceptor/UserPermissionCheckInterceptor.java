package org.meveo.apiv2.generic.security.interceptor;

import org.meveo.api.exception.AccessDeniedException;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.Set;

/**
 * Interceptor to check if the current user has the permission to execute an operation
 * on a given entity
 *
 * @author Mounir Boukayoua
 * @since 10.X
 */
public class UserPermissionCheckInterceptor {

    /**
     * class logger
     */
    private static final Logger log = LoggerFactory.getLogger(UserPermissionCheckInterceptor.class);

    /**
     * current user
     */
    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * Check if current user has the right permission to execute
     * a method on an entity
     *
     * @param context the method invocation context
     * @return the method result if check is Ok
     * @throws Exception {@link AccessDeniedException} is check is KO
     */
    @AroundInvoke
    public Object aroundInvoke(InvocationContext context) throws Exception {
        String entityName = getEntityName(context);
        String methodName = context.getMethod().getName();

        String operation;
        // find method is concidered as list operation
        if("find".equals(methodName)) {
            operation = "list";
        } else {
            operation = methodName;
        }

        String opPermission = "APIv2_" + entityName + "." + operation;
        String allPermission = "APIv2_" + entityName + ".all";
        Set<String> userRoles = currentUser.getRoles();

        if (userRoles.contains(opPermission) || userRoles.contains(allPermission) || userRoles.contains("APIv2_FULL_ACCESS")) {
            log.debug("Current user has the right permission to execute method={} on entity={}", methodName, entityName);
            return context.proceed();

        } else {
            log.debug("Current user has no permission to execute method={} on entity={}", methodName, entityName);
            throw new AccessDeniedException(String.format("Access to operation %s() on entity %s is not allowed.", methodName, entityName));
        }
    }

    /**
     * Get the entity name from the method's parameters
     *
     * @param context the method invocation context
     * @return the entity name
     */
    private String getEntityName(InvocationContext context) {
        Object firstParam = context.getParameters()[0];
        Class<?> entityClass;
        if (firstParam instanceof Class<?>) {
            entityClass = (Class<?>) firstParam;
        } else {
            entityClass = firstParam.getClass();
        }
        return entityClass.getSimpleName();
    }
}

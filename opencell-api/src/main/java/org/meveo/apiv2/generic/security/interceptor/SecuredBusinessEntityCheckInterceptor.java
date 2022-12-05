package org.meveo.apiv2.generic.security.interceptor;

import java.util.Map;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.SecuredBusinessEntityConfig;
import org.meveo.api.security.config.SecuredBusinessEntityConfigFactory;
import org.meveo.apiv2.generic.security.config.JsonConfigFactory;
import org.meveo.apiv2.generic.security.config.SecuredBusinessEntityJsonConfig;
import org.meveo.apiv2.generic.security.config.SecuredBusinessEntityJsonConfigFactory;
import org.meveo.commons.utils.ParamBeanFactory;

/**
 * This is the secured business entities interceptor which extends {@link SecuredBusinessEntityMethodInterceptor}, and uses {@link SecuredBusinessEntityJsonConfigFactory} to use the Json based config
 * 
 * @author mounir Boukayoua
 * @since 10.X
 */
public class SecuredBusinessEntityCheckInterceptor extends SecuredBusinessEntityMethodInterceptor {

    private static final long serialVersionUID = 3274783972583885289L;

    /**
     * Inject an instance of {@link SecuredBusinessEntityJsonConfigFactory}
     */
    @Inject
    @JsonConfigFactory
    protected SecuredBusinessEntityConfigFactory securedBusinessEntityConfigFactory;

    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    /**
     * Override aroundInvoke() method to get a json based config represented by an instance of {@link SecuredBusinessEntityJsonConfig} and then passe it to method checkForSecuredEntities()
     * 
     * @param context API method invocation context
     * @return API method result if the check is OK
     * @throws Exception an exception if check is KO
     */
    @AroundInvoke
    @Override
    public Object aroundInvoke(InvocationContext context) throws Exception {
        // Check if secured entities are enabled.
        if (!SecuredBusinessEntityMethodInterceptor.isSecuredEntitiesEnabled()) {
            return context.proceed();
        }
        SecuredBusinessEntityConfig sbeConfig = this.securedBusinessEntityConfigFactory.get(context);
        return super.checkForSecuredEntities(context, sbeConfig);
    }

    /**
     * Secure data model by enhancing search criteria and limiting access only to the secured entities as method configuration indicates
     * 
     * @param filters Search criteria to enhance
     * @param entityClass Class of an entity to secure
     * @throws AccessDeniedException Not able to grant access - a higher entity is being accessed, but user has access to lower entity only, OR user is searching excplicity for an entity that is not in teh list of
     *         accessible entities for the user
     */
    public void secureDataModel(Map<String, Object> filters, Class<?> entityClass) throws AccessDeniedException {

        if (!SecuredBusinessEntityMethodInterceptor.isSecuredEntitiesEnabled()) {
            return;
        }
        SecuredBusinessEntityConfig sbeConfig = this.securedBusinessEntityConfigFactory.get(entityClass, "list");
        if (sbeConfig == null) {
            return;
        }
        super.secureDataModel(filters, entityClass, sbeConfig);
    }
}

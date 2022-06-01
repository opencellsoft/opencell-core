package org.meveo.apiv2.generic.security.interceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

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
        boolean secureEntitesEnabled = paramBeanFactory.getInstance().getPropertyAsBoolean("secured.entities.enabled", true);
        if (!secureEntitesEnabled) {
            return context.proceed();
        }
        SecuredBusinessEntityConfig sbeConfig = this.securedBusinessEntityConfigFactory.get(context);
        return super.checkForSecuredEntities(context, sbeConfig);
    }
}
